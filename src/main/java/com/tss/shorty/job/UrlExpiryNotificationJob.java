package com.tss.shorty.job;

import com.tss.shorty.config.EmailConfig;
import com.tss.shorty.entity.Url;
import com.tss.shorty.repository.UrlRepository;
import com.tss.shorty.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UrlExpiryNotificationJob {
    private static final Logger log = LoggerFactory.getLogger(UrlExpiryNotificationJob.class);
    private final UrlRepository urlRepository;
    private final INotificationService notificationService;

    public UrlExpiryNotificationJob(UrlRepository urlRepository,
                                    INotificationService notificationService) {
        this.urlRepository = urlRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // runs every hour (sec, min, hour, day of month, month, day of week)
    public void processExpiredUrls() {
        log.info("notification job ran");
        List<Url> expiredUrls = urlRepository.findUrlsRequiringExpiryNotification(LocalDateTime.now());

        if (expiredUrls.isEmpty()) {
            log.info("No expired URLs found for notification.");
            return;
        }

        for (Url url : expiredUrls) {
            try {
                String userEmail = url.getUser().getEmail();
                String userName = url.getUser().getUserName();
                String subject = "Your Shorty URL has expired!";

                String emailBody = EmailConfig.getUrlExpiryTemplate(
                        userName != null ? userName : userEmail,
                        url.getShortUrl(), url.getOriginalUrl());
                notificationService.sendNotification(userEmail, subject, emailBody);
                log.info("email sent to user:{}, email:{}",userName, userEmail);

                url.setExpired(true);
                url.setExpiryNotified(true);
            } catch (Exception e) {
                log.error("error occurred while running. message:{}", e.getMessage());
            }

        }
        urlRepository.saveAll(expiredUrls);
        log.info("Successfully processed and notified {} expired URLs.", expiredUrls.size());
    }
}
