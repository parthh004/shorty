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

    public static String getRegistrationSuccessTemplate(String to) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + to + "</strong>,</p>" +
                "<p>Congratulations! Your email has been successfully verified, and your Shorty account is now active.</p>" +
                "<p>You can now log in to your dashboard and start shortening your URLs.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }
}
