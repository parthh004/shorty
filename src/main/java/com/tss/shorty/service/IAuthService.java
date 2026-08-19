package com.tss.shorty.service;

import com.tss.shorty.payload.request.*;
import com.tss.shorty.payload.response.AuthResponseDto;

public interface IAuthService
{
    AuthResponseDto register(RegistrationRequestDto registrationDTO);

    AuthResponseDto verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto);

    AuthResponseDto resendOtp(ResendOtpRequestDto resendOtpRequestDto);

    String login(LoginRequestDto loginRequestDto);

    AuthResponseDto logout(String token);

    AuthResponseDto forgotPassword(ForgotPasswordRequestDto forgotPasswordRequestDto);

    AuthResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDto);
}
