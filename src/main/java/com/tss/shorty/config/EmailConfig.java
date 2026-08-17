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

    public static String getAccountBlockedTemplate(String userName)
    {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + userName + "</strong>,</p>" +
                "<p>We regret to inform you that your Shorty account has been <strong>blocked</strong> by an administrator due to a violation of our terms of service or suspicious/illegal activities.</p>" +
                "<p>If you believe this is a mistake, please contact support.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }

    public static String getAccountUnblockedTemplate(String userName)
    {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;\">" +
                "<p>Dear <strong>" + userName + "</strong>,</p>" +
                "<p>Good news! Your Shorty account has been <strong>unblocked</strong> by an administrator. You can now log in and continue using our services.</p>" +
                "<p>Please ensure you adhere to our terms of service to avoid future interruptions.</p>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "</div>";
    }


    public static String getAuditLogExportTemplate(String userName) {
        return "<div style=\"font-family: Arial, sans-serif; padding: 20px; line-height: 1.6; color: #333;\">" +
                "<h2 style=\"color: #0056b3;\">Audit Logs Export Complete</h2>" +
                "<p>Dear <strong>" + userName + "</strong>,</p>" +
                "<p>Your requested Audit Logs report has been successfully generated.</p>" +
                "<p>Please find the attached <strong>CSV file</strong> containing the system activity logs based on your selected filters. You can easily open this file using Microsoft Excel, Google Sheets, or any standard spreadsheet software to search, sort, and analyze the data.</p>" +
                "<br>" +
                "<p>Best Regards,<br><strong>The Shorty Team</strong></p>" +
                "<div style=\"font-size: 12px; color: #777; margin-top: 30px; padding-top: 15px; border-top: 1px solid #eee;\">" +
                "<em><strong>Security Notice:</strong> This report contains sensitive system activity. Please handle this file securely and in accordance with your organization's data privacy policies.</em>" +
                "</div>" +
                "</div>";
    }
}
