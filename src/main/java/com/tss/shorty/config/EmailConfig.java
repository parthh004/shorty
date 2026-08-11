package com.tss.shorty.config;

public class EmailConfig
{
    public static String getOtpMessageTemplate(String to, String otp)
    {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + to + "</strong>,</p>" +
                "<p>Thank you for registering with Shorty! To complete your account setup, please verify your email address.</p>" +
                "<p>your otp : <span style=\"font-size: 18px; font-weight: bold; background-color: #f4f4f4; padding: 4px 8px; border-radius: 4px;\">" + otp + "</span></p>" +
                "<p>This OTP is valid for 10 minutes. Please do not share it with anyone.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }

    public static String getPasswordResetTemplate(String to, String otp)
    {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + to + "</strong>,</p>" +
                "<p>We received a request to reset the password for your Shorty account.</p>" +
                "<p>Your password reset OTP is: <span style=\"font-size: 18px; font-weight: bold; background-color: #f4f4f4; padding: 4px 8px; border-radius: 4px;\">" + otp + "</span></p>" +
                "<p>This OTP is valid for 10 minutes. If you did not request a password reset, please ignore this email.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }
}
