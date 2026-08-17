package com.tss.shorty.mapper;

import com.tss.shorty.entity.Url;
import com.tss.shorty.payload.response.UrlAliasCheckResponseDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UrlMapper {
    @Mapping(source = "id", target = "urlId")
    UrlResponseDto mapToUrlCreateResponse(Url url);

    @Mapping(source = "id", target = "urlId")
    @Mapping(source = "customAlias", target = "customAlias")
    UrlDetailResponseDto mapToUrlDetails(Url url);

    @Mapping(source = "isAvailable", target = "available")
    UrlAliasCheckResponseDto mapToAliasCheckResponse(String alias, Boolean isAvailable);
}
