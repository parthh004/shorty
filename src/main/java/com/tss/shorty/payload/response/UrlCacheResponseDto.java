package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlCacheResponseDto {
    UUID id;
    String originalUrl;
    int remainingVisits;
    LocalDateTime expiryDate;
}
