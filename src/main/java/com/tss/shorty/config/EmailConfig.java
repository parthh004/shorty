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

    public static String getUrlExpiryTemplate(String to, String shortUrl, String originalUrl) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + to + "</strong>,</p>" +
                "<p>We are writing to let you know that your shortened URL has expired or reached its maximum visit limit.</p>" +
                "<p><strong>Short URL:</strong> <a href=\"http://localhost:8080/" + shortUrl + "\">" + shortUrl + "</a><br>" +
                "<strong>Destination:</strong> " + originalUrl + "</p>" +
                "<p>If you wish to reactivate this link or add more visits, please log in to your Shorty dashboard and upgrade your limits.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }
}
