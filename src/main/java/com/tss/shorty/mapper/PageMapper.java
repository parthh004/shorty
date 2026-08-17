package com.tss.shorty.mapper;

import com.tss.shorty.payload.response.PaginatedDto;
import org.springframework.data.domain.Page;

public class PageMapper {
    public static <T> PaginatedDto<T> toPaginatedDto(Page<T> page) {
        return new PaginatedDto<>(page);
    }
}
