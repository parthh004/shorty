package com.tss.shorty.payload.response;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto
{
    private LocalDateTime timestamp;
    private int status;
    private String message;
}
