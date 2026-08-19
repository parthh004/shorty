package com.tss.shorty.controller;

import com.tss.shorty.payload.response.PricingResponseDto;
import com.tss.shorty.repository.ConfigRepository;
import com.tss.shorty.service.ConfigService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing")
@PreAuthorize("permitAll()")
public class PricingConfigController {
    private final ConfigService configService;

    public PricingConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    @Cacheable(value = "pricing")
    public ResponseEntity<PricingResponseDto> getPricing() {
        PricingResponseDto responseDto = configService.getPricing();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
