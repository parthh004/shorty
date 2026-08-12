package com.tss.shorty.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UrlRequestDto {
    @NotBlank(message = "Link is required")
    @NotNull(message = "Link is required")
    private String originalUrl;

    @NotBlank(message = "alias cannot be blank")
    private String customAlias;
}
