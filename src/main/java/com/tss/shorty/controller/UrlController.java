package com.tss.shorty.controller;

import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import com.tss.shorty.service.CurrentUserProvider;
import com.tss.shorty.service.IUrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {
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
    }}
