package com.tss.shorty.service;

public interface INotificationService
{
    void sendNotification(String receiver, String subject, String messageHtml);
}