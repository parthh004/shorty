package com.tss.shorty.controller;

import com.tss.shorty.payload.request.*;
import com.tss.shorty.payload.response.AuthResponseDto;
import com.tss.shorty.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController
{
    private final IAuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(IAuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestPart(value = "data") RegistrationRequestDto registrationDto, @RequestPart(required = false, value = "image")MultipartFile image)
    {
        registrationDto.setImage(image);
        AuthResponseDto response = authService.register(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegistrationRequestDto registrationDto)
    {
        AuthResponseDto response = authService.register(registrationDto);
        log.info("user register ongoing");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request)
    {
        AuthResponseDto response = authService.verifyOtp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<AuthResponseDto> resendOtp(@Valid @RequestBody ResendOtpRequestDto resendOtpRequestDto)
    {
        AuthResponseDto response = authService.resendOtp(resendOtpRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
    {
        String token = authService.login(loginRequestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        AuthResponseDto registrationResponseDto = AuthResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .build();

        return ResponseEntity.ok().headers(headers).body(registrationResponseDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logout(HttpServletRequest request)
    {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            String token = bearerToken.substring(7);
            AuthResponseDto response = authService.logout(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Invalid or missing token.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDto> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto)
    {
        AuthResponseDto response = authService.forgotPassword(forgotPasswordRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequestDto resetPasswordRequestDto)
    {
        AuthResponseDto response = authService.resetPassword(resetPasswordRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}