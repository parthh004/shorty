package com.tss.shorty.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UrlUpdateDto {

    @NotBlank(message = "Original URL cannot be empty")
    private String originalUrl;

}