package com.tss.shorty.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponseDto
{
    private LocalDateTime timestamp;
    private int status;
    private String message;
}
