package com.tss.shorty.service;

import com.tss.shorty.payload.request.LoginRequestDto;
import com.tss.shorty.payload.request.RegistrationRequestDto;
import com.tss.shorty.payload.request.ResendOtpRequestDto;
import com.tss.shorty.payload.request.VerifyOtpRequestDto;
import com.tss.shorty.payload.response.RegistrationResponseDto;

public interface IAuthService
{
    RegistrationResponseDto register(RegistrationRequestDto registrationDTO);

    RegistrationResponseDto verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto);

    RegistrationResponseDto resendOtp(ResendOtpRequestDto resendOtpRequestDto);

    String login(LoginRequestDto loginRequestDto);

    RegistrationResponseDto logout(String token);
}
