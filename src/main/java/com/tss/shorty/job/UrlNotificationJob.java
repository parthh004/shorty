package com.tss.shorty.job;

import com.tss.shorty.config.EmailConfig;
import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.repository.OtpRepository;
import com.tss.shorty.repository.UserRepository;
import com.tss.shorty.repository.UrlRepository;
import com.tss.shorty.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UrlNotificationJob {
    private static final Logger log = LoggerFactory.getLogger(UrlNotificationJob.class);
    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final INotificationService notificationService;

    public UrlNotificationJob(UrlRepository urlRepository,
                              OtpRepository otpRepository,
                              UserRepository userRepository,
                              INotificationService notificationService) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.otpRepository = otpRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *") // runs every hour (sec, min, hour, day of month, month, day of week)
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
                log.info("email sent to user:{}, email:{}", userName, userEmail);

                url.setExpired(true);
                url.setExpiryNotified(true);
            } catch (Exception e) {
                log.error("error occurred while running. message:{}", e.getMessage());
            }

        }
        urlRepository.saveAll(expiredUrls);
        log.info("Successfully processed and notified {} expired URLs.", expiredUrls.size());
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldOtp() {
        log.info("otp cleanup job ran");
        // Delete OTPs that expired more than 24 hours ago
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        int deletedCount = otpRepository.deleteOldAndUsedOtp(cutoff);
        System.out.println("Cleaned up " + deletedCount + " old OTPs from the database.");
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanUpUnverifiedUser() {
        log.info("user cleanup job ran");
        List<User> unverifiedUsers = userRepository.findByIsEmailVerifiedFalse();
        if (unverifiedUsers.isEmpty()) {
            log.info("No Unverified User found for notification.");
            return;
        }
        List<User> errorUser = new ArrayList<>();
        for (User user : unverifiedUsers) {
            try {
                String userEmail = user.getEmail();
                String userName = user.getUserName();
                String subject = "Your Account is getting deleted!";

                String emailBody = EmailConfig.getAccountDeletionTemplate(
                        userName != null ? userName : userEmail);
                notificationService.sendNotification(userEmail, subject, emailBody);
                log.info("email sent to user:{}, email:{}", userName, userEmail);

            } catch (Exception e) {
                log.error("error occurred while running. message:{}", e.getMessage());
                errorUser.add(user);
            }

        }
        userRepository.deleteAll(unverifiedUsers);
        log.info("Successfully processed and notified {} users for deletion.", unverifiedUsers.size() - errorUser.size());
        log.info("Error occurred while notifying {} users for deletion.", errorUser.size());
    }
}
