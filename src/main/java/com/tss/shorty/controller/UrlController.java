package com.tss.shorty.controller;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.request.UrlUpdateDto;
import com.tss.shorty.payload.response.*;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.IUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/urls")
@PreAuthorize("hasRole('USER')")
public class UrlController {
    private static final Logger log = LoggerFactory.getLogger(UrlController.class);
    private final IUrlService urlService;
    private final CurrentUserProvider currentUserProvider;

    public UrlController(IUrlService urlService, CurrentUserProvider currentUserProvider) {
        this.urlService = urlService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> createUrl(@RequestBody UrlRequestDto requestDto) {
        User user = currentUserProvider.get();
        UrlResponseDto responseDto = urlService.createUrl(requestDto, user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{urlId}")
    public ResponseEntity<UrlDetailResponseDto> getUrlDetails(@PathVariable UUID urlId) {
        User user = currentUserProvider.get();
        UrlDetailResponseDto response = urlService.getUrlDetails(urlId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{urlId}")
    public ResponseEntity<Void> updateUrlDetails(@RequestBody UrlUpdateDto updateDto, @PathVariable UUID urlId) {
        User user = currentUserProvider.get();

        urlService.updateUrlDetails(user, updateDto, urlId);
        log.info("url updated");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{urlId}")
    public ResponseEntity<Void> deleteUrlDetails(@PathVariable UUID urlId) {
        User user = currentUserProvider.get();
        urlService.deleteUrlDetails(user, urlId);
        log.info("url deleted");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/check-alias")
    public ResponseEntity<UrlAliasCheckResponseDto> checkAlias(@RequestParam String alias) {
        UrlAliasCheckResponseDto responseDto = urlService.checkAliasAvailable(alias);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedDto<UrlDetailResponseDto>> getAllUrl(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(value = "expiryDate", required = false)
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate expiryDate,

            @RequestParam(value = "lastAccessedDate", required = false)
            @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate lastAccessedDate,

            @RequestParam(value = "customAlias", required = false) Boolean hasCustomAlias,
            @RequestParam(value = "isExpired", required = false) Boolean hasExpired,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PaginatedDto<UrlDetailResponseDto> paginatedDto = urlService.getAll(
                currentUser, expiryDate, lastAccessedDate, hasCustomAlias, hasExpired, pageable);

        return new ResponseEntity<>(paginatedDto, HttpStatus.OK);
    }
}
