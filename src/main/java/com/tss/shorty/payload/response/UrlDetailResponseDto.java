package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDetailResponseDto {
    private UUID urlId;
    private String shortUrl;
    private String originalUrl;
    private boolean customAlias;

    // Analytics
    private Integer visitLimit;
    private Integer remainingVisits;
    private Integer totalVisits;

    // Timestamps
    private LocalDateTime expiryDate;
    private LocalDateTime lastAccessedOn;
}
