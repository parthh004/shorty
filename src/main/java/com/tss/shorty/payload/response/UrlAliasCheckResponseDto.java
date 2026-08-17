package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlAliasCheckResponseDto {
    String alias;
    Boolean available;
}
