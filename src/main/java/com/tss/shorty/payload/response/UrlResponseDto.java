package com.tss.shorty.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@NoArgsConstructor
public class UrlResponseDto {
    private String shortUrl;
    private Integer remainingVisits;
    private LocalDateTime expiryDate;

}
