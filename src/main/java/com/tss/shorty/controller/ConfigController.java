package com.tss.shorty.controller;

import com.tss.shorty.payload.response.SystemConfigResponseDto;
import com.tss.shorty.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/configs")
@PreAuthorize("hasRole('ADMIN')")
public class ConfigController
{
    private final ConfigService configService;

    public ConfigController(ConfigService configService)
    {
        this.configService = configService;
    }

    @PatchMapping("/{code}")
    public ResponseEntity<SystemConfigResponseDto> updateConfig(@PathVariable("code") String code, @RequestParam("value") String value)
    {
        code = code.toUpperCase();
        return ResponseEntity.ok(configService.updateConfig(code, value));
    }
}