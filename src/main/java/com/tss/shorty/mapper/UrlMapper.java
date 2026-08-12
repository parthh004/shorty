package com.tss.shorty.mapper;

import com.tss.shorty.entity.Url;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    UrlResponseDto mapToUrlCreateResponse(Url url);

    UrlDetailResponseDto mapToUrlDetails(Url url);
}
