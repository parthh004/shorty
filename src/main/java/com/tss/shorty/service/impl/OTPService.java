package com.tss.shorty.service.impl;

import com.tss.shorty.config.EmailConfig;
import com.tss.shorty.entity.Otp;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.OtpType;
import com.tss.shorty.repository.OtpRepository;
import com.tss.shorty.service.INotificationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OTPService
{
    @Getter
    private final OtpRepository otpRepository;
    private final INotificationService notificationService;
    private final SecureRandom random = new SecureRandom();

    public OTPService(OtpRepository otpRepository, @Qualifier("emailNotificationService") INotificationService notificationService)
    {
        this.otpRepository = otpRepository;
        this.notificationService = notificationService;
    }

    public void generateAndSendOtp(User user, OtpType type)
    {
        String otpCode = String.valueOf(100000 + random.nextInt(900000));
        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setType(type);
        otp.setCreatedOn(LocalDateTime.now());
        otp.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otp);

        String subject;
        String emailContent;

        if (type == OtpType.PASSWORD_RESET)
        {
            subject = "Shorty - Password Reset Request";
            emailContent = EmailConfig.getPasswordResetTemplate(user.getUserName(), otpCode);
        }
        else
        {
            subject = "Shorty - Verify Your Email";
            emailContent = EmailConfig.getOtpMessageTemplate(user.getUserName(), otpCode);
        }
        notificationService.sendNotification(user.getEmail(), subject, emailContent);
    }

    public boolean verifyOtp(User user, String otpCode, OtpType type)
    {
        Otp otp = otpRepository.findTopByUserAndTypeOrderByCreatedOnDesc(user, type).orElseThrow(() -> new IllegalArgumentException("No OTP request found."));

        if (otp.isUsed())
        {
            throw new IllegalArgumentException("This OTP has already been used.");
        }

        if (otp.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            throw new IllegalArgumentException("OTP has expired. Please request a new one.");
        }

        if (otp.getAttemptCount() >= 3)
        {
            throw new IllegalArgumentException("Maximum attempts reached. Please request a new OTP.");
        }

        if (!otp.getOtpCode().equals(otpCode))
        {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);
            throw new IllegalArgumentException("Invalid OTP.");
        }
        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }
}