package com.tss.shorty.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
public class UrlResponseDto {
    private UUID urlId;
    private String shortUrl;
    private Integer remainingVisits;
    private LocalDateTime expiryDate;

}
