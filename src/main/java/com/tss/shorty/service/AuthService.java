package com.tss.shorty.service;

import com.tss.shorty.entity.TokenBlacklist;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.OtpType;
import com.tss.shorty.entity.enums.Role;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.mapper.IUserMapper;
import com.tss.shorty.payload.request.*;
import com.tss.shorty.payload.response.RegistrationResponseDto;
import com.tss.shorty.repository.ITokenBlacklistRepository;
import com.tss.shorty.repository.IUserRepository;
import com.tss.shorty.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService
{
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserMapper userMapper;
    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ITokenBlacklistRepository tokenBlacklistRepository;

    @Transactional
    @Override
    public RegistrationResponseDto register(RegistrationRequestDto registrationDto)
    {
        if (userRepository.existsByEmail(registrationDto.getEmail()))
        {
            throw new IllegalArgumentException("Email is already in use!");
        }

        if (userRepository.existsByPhoneNo(registrationDto.getPhoneNo()))
        {
            throw new IllegalArgumentException("Phone number is already in use!");
        }

        User user = userMapper.toUserEntity(registrationDto);

        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        user.setRole(Role.ROLE_USER);
        user.setIsActive(false);
        user.setIsEmailVerified(false);
        user.setAvailableSlots(5);
        userRepository.save(user);

        otpService.generateAndSendOtp(user, OtpType.EMAIL_VERIFICATION);

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully! Please verify your email.")
                .build();
    }

    @Override
    public RegistrationResponseDto verifyOtp(VerifyOtpRequestDto request)
    {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (user.getIsEmailVerified())
        {
            throw new IllegalArgumentException("Email is already verified. You can log in.");
        }

        otpService.verifyOtp(user, request.getOtp(), OtpType.EMAIL_VERIFICATION);
        user.setIsEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Email verified successfully! You can now log in.")
                .build();
    }


    @Override
    public RegistrationResponseDto resendOtp(ResendOtpRequestDto resendOtpRequestDto)
    {
        User user = userRepository.findByEmail(resendOtpRequestDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + resendOtpRequestDto.getEmail()));

        if (user.getIsEmailVerified())
        {
            throw new IllegalArgumentException("Email is already verified. You can log in.");
        }

        LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        int dailyOtpCount = otpService.getOtpRepository().countByUserAndTypeAndCreatedOnAfter(user, OtpType.EMAIL_VERIFICATION, startOfDay);

        if (dailyOtpCount >= 5)
        {
            throw new IllegalArgumentException("Daily OTP limit reached. Please try again tomorrow.");
        }

        otpService.getOtpRepository().findTopByUserAndTypeOrderByCreatedOnDesc(user, OtpType.EMAIL_VERIFICATION)
                .ifPresent(lastOtp -> {

                    LocalDateTime oneMinuteAfterCreation = lastOtp.getCreatedOn().plusMinutes(1);

                    if (LocalDateTime.now().isBefore(oneMinuteAfterCreation))
                    {
                        throw new IllegalArgumentException("Please wait at least 1 minute before requesting a new OTP.");
                    }
                });

        otpService.generateAndSendOtp(user, OtpType.EMAIL_VERIFICATION);

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("A new OTP has been sent to your email address.")
                .build();
    }

    @Override
    public String login(LoginRequestDto loginRequestDto)
    {
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequestDto.getEmail()));

        if (!user.getIsEmailVerified() || !user.getIsActive())
        {
            throw new IllegalArgumentException("Account is not verified or inactive. Please verify your email.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public RegistrationResponseDto logout(String token)
    {
        String tokenId = jwtTokenProvider.getTokenIdFromToken(token);

        if(!tokenBlacklistRepository.existsById(tokenId))
        {
            TokenBlacklist tokenBlacklist = new TokenBlacklist(tokenId,jwtTokenProvider.getExpirationDateFromToken(token));
            tokenBlacklistRepository.save(tokenBlacklist);
        }

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Logged out successfully.")
                .build();
    }

    @Override
    public RegistrationResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto)
    {
        User user = userRepository.findByEmail(forgotPasswordRequestDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + forgotPasswordRequestDto.getEmail()));

        if (!user.getIsActive() || !user.getIsEmailVerified())
        {
            throw new IllegalArgumentException("Account is inactive or unverified.");
        }

        // Daily Hard Limit (Max 5 Password Reset OTPs per day)
        LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        int dailyOtpCount = otpService.getOtpRepository().countByUserAndTypeAndCreatedOnAfter(user, OtpType.PASSWORD_RESET, startOfDay);

        if (dailyOtpCount >= 5)
        {
            throw new IllegalArgumentException("Daily OTP limit reached. Please try again tomorrow.");
        }

        // 60-Second Cooldown Timer Check
        otpService.getOtpRepository().findTopByUserAndTypeOrderByCreatedOnDesc(user, OtpType.PASSWORD_RESET)
                .ifPresent(lastOtp -> {
                    LocalDateTime oneMinuteAfterCreation = lastOtp.getCreatedOn().plusMinutes(1);
                    if (LocalDateTime.now().isBefore(oneMinuteAfterCreation)) {
                        throw new IllegalArgumentException("Please wait at least 1 minute before requesting a new OTP.");
                    }
                });

        otpService.generateAndSendOtp(user, OtpType.PASSWORD_RESET);

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Password reset OTP has been sent to your email address.")
                .build();
    }

    @Override
    public RegistrationResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto)
    {
        User user = userRepository.findByEmail(resetPasswordRequestDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + resetPasswordRequestDto.getEmail()));

        otpService.verifyOtp(user, resetPasswordRequestDto.getOtp(), OtpType.PASSWORD_RESET);

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);

        return RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Password has been reset successfully. You can now log in with your new password.")
                .build();
    }
}