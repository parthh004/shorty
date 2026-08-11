package com.tss.shorty.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailNotificationService")
public class EmailNotificationService implements INotificationService
{
    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(String receiver, String subject, String messageHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(receiver);
            helper.setSubject(subject);
            helper.setText(messageHtml, true);

            mailSender.send(message);
            log.info("HTML Email sent successfully to: {}", receiver);

        }
        catch (MessagingException e)
        {
            log.error("Failed to send email to {}: {}", receiver, e.getMessage());
            throw new RuntimeException("Failed to send email notification");
        }
    }
}