package com.tss.shorty.service;

import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.payload.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface IAdminService {
    UserProfileResponseDto blockUser(UUID userId);

    UserProfileResponseDto unblockUser(UUID userId);

    UrlDetailResponseDto blockUrl(UUID urlId);

    UrlDetailResponseDto unblockUrl(UUID urlId);

    PaginatedDto<UrlDetailResponseDto> getUserUrls(UUID userId, Pageable pageable);

    PaginatedDto<AuditLogResponseDto> getAuditLogs(AuditAction action, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void exportAuditLogsAsync(AuditAction action, LocalDate startDate, LocalDate endDate, User admin);

    PaginatedDto<UserSummaryDto> getAllUsers(String searchName, String searchEmail, Boolean isActive, Pageable pageable);
}
