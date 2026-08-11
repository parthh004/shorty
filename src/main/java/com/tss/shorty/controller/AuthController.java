package com.tss.shorty.controller;

import com.tss.shorty.payload.request.LoginRequestDto;
import com.tss.shorty.payload.request.RegistrationRequestDto;
import com.tss.shorty.payload.request.ResendOtpRequestDto;
import com.tss.shorty.payload.request.VerifyOtpRequestDto;
import com.tss.shorty.payload.response.RegistrationResponseDto;
import com.tss.shorty.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController
{
    private final IAuthService authService;

    public AuthController(IAuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@Valid @RequestBody RegistrationRequestDto registrationDto)
    {
        RegistrationResponseDto response = authService.register(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<RegistrationResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request)
    {
        RegistrationResponseDto response = authService.verifyOtp(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<RegistrationResponseDto> resendOtp(@Valid @RequestBody ResendOtpRequestDto resendOtpRequestDto)
    {
        RegistrationResponseDto response = authService.resendOtp(resendOtpRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<RegistrationResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
    {
        String token = authService.login(loginRequestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        RegistrationResponseDto registrationResponseDto = RegistrationResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .build();

        return ResponseEntity.ok().headers(headers).body(registrationResponseDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<RegistrationResponseDto> logout(HttpServletRequest request)
    {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
        {
            String token = bearerToken.substring(7);
            RegistrationResponseDto response = authService.logout(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new IllegalArgumentException("Invalid or missing token.");
    }
}