package com.tss.shorty.payload.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponseDto {
    private Integer availableSlots;
    private Long totalUrlsCreated;
    private Long activeUrls;
    private Long totalClicksReceived;
}
