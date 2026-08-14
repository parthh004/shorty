package com.tss.shorty.controller;


import com.tss.shorty.entity.User;
import com.tss.shorty.entity.enums.AuditAction;
import com.tss.shorty.payload.request.AuditLogExportRequestDto;
import com.tss.shorty.payload.response.*;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.IAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController
{
    private final IAdminService adminService;
    private final CurrentUserProvider currentUserProvider;

    public AdminController(IAdminService adminService, CurrentUserProvider currentUserProvider)
    {
        this.adminService = adminService;
        this.currentUserProvider = currentUserProvider;
    }
    @GetMapping("/users")
    public ResponseEntity<PaginatedDto<UserSummaryDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(page = 0, size = 10, sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return ResponseEntity.ok(adminService.getAllUsers(name, email, isActive, pageable));
    }

    @PatchMapping("/users/{id}/block")
    public ResponseEntity<UserProfileResponseDto> blockUser(@PathVariable("id") UUID userId)
    {
        return ResponseEntity.ok(adminService.blockUser(userId));
    }

    @PatchMapping("/users/{id}/unblock")
    public ResponseEntity<UserProfileResponseDto> unblockUser(@PathVariable("id") UUID userId)
    {
        return ResponseEntity.ok(adminService.unblockUser(userId));
    }

    @PatchMapping("/urls/{id}/block")
    public ResponseEntity<UrlDetailResponseDto> blockUrl(@PathVariable("id") UUID urlId)
    {
        return ResponseEntity.ok(adminService.blockUrl(urlId));
    }

    @PatchMapping("/urls/{id}/unblock")
    public ResponseEntity<UrlDetailResponseDto> unblockUrl(@PathVariable("id") UUID urlId)
    {
        return ResponseEntity.ok(adminService.unblockUrl(urlId));
    }

    @GetMapping("/users/{id}/urls")
    public ResponseEntity<PaginatedDto<UrlDetailResponseDto>> getUserUrls(@PathVariable("id") UUID userId, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return ResponseEntity.ok(adminService.getUserUrls(userId, pageable));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<PaginatedDto<AuditLogResponseDto>> getAuditLogs(
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate,
            @PageableDefault(page = 0, size = 10, sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return ResponseEntity.ok(adminService.getAuditLogs(action, startDate, endDate, pageable));
    }

}
