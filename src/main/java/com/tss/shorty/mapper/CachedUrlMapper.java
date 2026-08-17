package com.tss.shorty.mapper;

import com.tss.shorty.entity.Url;
import com.tss.shorty.payload.response.UrlCacheResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CachedUrlMapper {
    @Mapping(source = "id", target = "id")
    UrlCacheResponseDto mapToCachedUrl(Url url);
}
