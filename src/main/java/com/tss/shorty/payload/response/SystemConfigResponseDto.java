package com.tss.shorty.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigResponseDto
{
    private String code;
    private String description;
    private String value;
    private String type;
}