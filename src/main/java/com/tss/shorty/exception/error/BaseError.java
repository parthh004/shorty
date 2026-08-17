package com.tss.shorty.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
}