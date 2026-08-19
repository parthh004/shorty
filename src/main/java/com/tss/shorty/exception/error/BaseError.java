package com.tss.shorty.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String path;

    public BaseError(Integer status, String error) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.path = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
    }
}