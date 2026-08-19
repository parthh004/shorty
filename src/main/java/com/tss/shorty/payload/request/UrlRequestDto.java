package com.tss.shorty.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {
    @NotBlank(message = "Link is required")
    @NotNull(message = "Link is required")
    private String originalUrl;

    @Size(min = 4, max = 7, message = "Alias must be between 4 and 7 characters")
    private String alias;
}
