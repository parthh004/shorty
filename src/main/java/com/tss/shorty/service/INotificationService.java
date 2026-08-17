package com.tss.shorty.service;

public interface INotificationService
{
    void sendNotification(String receiver, String subject, String messageHtml);

    void sendEmailWithAttachment(String receiver, String subject, String messageHtml, byte[] attachmentData, String attachmentFilename);
}