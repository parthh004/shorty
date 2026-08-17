package com.tss.shorty.service.impl;

import com.tss.shorty.annotation.AuditActivity;
import com.tss.shorty.config.EmailConfig;
import com.tss.shorty.entity.AuditLog;
import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.entity.enums.Outcome;
import com.tss.shorty.entity.enums.Role;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.mapper.AuditMapper;
import com.tss.shorty.mapper.PageMapper;
import com.tss.shorty.mapper.UrlMapper;
import com.tss.shorty.mapper.UserMapper;
import com.tss.shorty.payload.response.*;
import com.tss.shorty.repository.AuditLogRepository;
import com.tss.shorty.repository.UserRepository;
import com.tss.shorty.repository.UrlRepository;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.IAdminService;
import com.tss.shorty.service.INotificationService;
import com.tss.shorty.util.AuditLogSpecification;
import com.tss.shorty.util.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AdminService implements IAdminService
{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final INotificationService notificationService;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final AuditLogRepository auditLogRepository;
    private final AuditMapper auditMapper;

    public AdminService(UserRepository userRepository, UserMapper userMapper, @Qualifier("emailNotificationService")INotificationService notificationService, UrlRepository urlRepository, UrlMapper urlMapper, AuditLogRepository auditLogRepository, AuditMapper auditMapper)
    {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
        this.auditLogRepository = auditLogRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    public PaginatedDto<UserSummaryDto> getAllUsers(String searchName, String searchEmail, Boolean isActive, Pageable pageable) {

        Specification<User> spec = Specification.where(UserSpecification.hasRole(Role.ROLE_USER))
                .and(UserSpecification.nameContains(searchName))
                .and(UserSpecification.emailContains(searchEmail))
                .and(UserSpecification.isActive(isActive));

        Page<User> userPage = userRepository.findByRole(Role.ROLE_USER,spec, pageable);
        Page<UserSummaryDto> dtoPage = userPage.map(userMapper::toUserSummaryDto);
        return PageMapper.toPaginatedDto(dtoPage);
    }

    @Override
    @Transactional
    @AuditActivity(action = AuditAction.USER_BLOCKED, targetEntity = "USER")
    public UserProfileResponseDto blockUser(UUID userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User is already blocked.");
        }

        user.setIsActive(false);
        User savedUser = userRepository.save(user);

        String subject = "Important: Your Shorty Account has been Blocked";
        String emailContent = EmailConfig.getAccountBlockedTemplate(user.getUserName());
        notificationService.sendNotification(user.getEmail(), subject, emailContent);

        log.info("Successfully blocked user ID: {}", userId);

        return userMapper.toUserProfileResponseDto(savedUser);
    }

    @Override
    @Transactional
    @AuditActivity(action = AuditAction.USER_UNBLOCKED, targetEntity = "USER")
    public UserProfileResponseDto unblockUser(UUID userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getIsActive()) {
            throw new IllegalArgumentException("User is already active.");
        }

        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        String subject = "Welcome Back! Your Shorty Account is Active Again";
        String emailContent = EmailConfig.getAccountUnblockedTemplate(user.getUserName());
        notificationService.sendNotification(user.getEmail(), subject, emailContent);
        log.info("Successfully unblocked user ID: {}", userId);

        return userMapper.toUserProfileResponseDto(savedUser);

    }

    @Override
    @Transactional
    @AuditActivity(action = AuditAction.URL_BLOCKED, targetEntity = "URL")
    public UrlDetailResponseDto blockUrl(UUID urlId)
    {
        Url url = urlRepository.findById(urlId).orElseThrow(() -> new ResourceNotFoundException("URL not found with ID: " + urlId));

        if (!url.isActive()) {
            throw new IllegalArgumentException("URL is already blocked.");
        }

        url.setActive(false);
        Url savedUrl = urlRepository.save(url);
        log.info("Successfully block url ID: {}", urlId);

        return urlMapper.mapToUrlDetails(savedUrl);
    }

    @Override
    @Transactional
    @AuditActivity(action = AuditAction.URL_UNBLOCKED, targetEntity = "URL")
    public UrlDetailResponseDto unblockUrl(UUID urlId)
    {
        Url url = urlRepository.findById(urlId).orElseThrow(() -> new ResourceNotFoundException("URL not found with ID: " + urlId));

        if (url.isActive()) {
            throw new IllegalArgumentException("URL is already active.");
        }

        url.setActive(true);
        Url savedUrl = urlRepository.save(url);
        log.info("Successfully unblock url ID: {}", urlId);

        return urlMapper.mapToUrlDetails(savedUrl);
    }

    @Override
    public PaginatedDto<UrlDetailResponseDto> getUserUrls(UUID userId, Pageable pageable)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Page<Url> urlPage = urlRepository.findByUser(user, pageable);
        Page<UrlDetailResponseDto> dtoPage = urlPage.map(urlMapper::mapToUrlDetails);
        return PageMapper.toPaginatedDto(dtoPage);
    }

    @Override
    public PaginatedDto<AuditLogResponseDto> getAuditLogs(AuditAction action, LocalDate startDate, LocalDate endDate, Pageable pageable)
    {
        Specification<AuditLog> specification = Specification.where(AuditLogSpecification.hasAction(action))
                .and(AuditLogSpecification.fromDate(startDate))
                .and(AuditLogSpecification.toDate(endDate));

        Page<AuditLog> page = auditLogRepository.findAll(specification, pageable);
        return PageMapper.toPaginatedDto(page.map(auditMapper::toDto));
    }

    @Async
    @Override
    public void exportAuditLogsAsync(AuditAction action, LocalDate startDate, LocalDate endDate, User admin) {

        log.info("Starting background Audit Log CSV export for user: {}", admin.getEmail());

        Specification<AuditLog> specification = Specification.where(AuditLogSpecification.hasAction(action))
                .and(AuditLogSpecification.fromDate(startDate))
                .and(AuditLogSpecification.toDate(endDate));

        List<AuditLog> logs = auditLogRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdOn"));

        StringBuilder sb = new StringBuilder();
        sb.append("Log ID,Date,Actor,Action,Target Entity,Target ID,Outcome\n");

        for (AuditLog logRecord : logs) {
            sb.append(logRecord.getId()).append(",")
                    .append(logRecord.getCreatedOn()).append(",")
                    .append(logRecord.getActor()).append(",")
                    .append(logRecord.getAction()).append(",")
                    .append(logRecord.getTargetEntity()).append(",")
                    .append(logRecord.getTargetId()).append(",")
                    .append(logRecord.getOutcome()).append("\n");
        }

        byte[] fileData = sb.toString().getBytes(StandardCharsets.UTF_8);
        String fileName = "AuditLogs_Export_" + LocalDate.now().toString() + ".csv";

        String htmlBody = EmailConfig.getAuditLogExportTemplate(admin.getUserName());
        notificationService.sendEmailWithAttachment(admin.getEmail(), "Your Audit Logs Export", htmlBody, fileData, fileName);
    }
}
