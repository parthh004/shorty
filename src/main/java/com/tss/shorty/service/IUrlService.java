package com.tss.shorty.service;

import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.request.UrlUpdateDto;
import com.tss.shorty.payload.response.PaginatedDto;
import com.tss.shorty.payload.response.UrlAliasCheckResponseDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface IUrlService {
    UrlResponseDto createUrl(UrlRequestDto requestDto, User user);

    UrlDetailResponseDto getUrlDetails(UUID urlId, User user);

    void updateUrlDetails(User user, UrlUpdateDto updateDto, UUID urlId);

    void deleteUrlDetails(User user, UUID urlId);

    UrlAliasCheckResponseDto checkAliasAvailable(String alias);

    void reinstateUrl(UUID id);

    String getOriginalUrl(String shortUrl);

    PaginatedDto<UrlDetailResponseDto> getAll(User currentUser, LocalDate expiryDate, LocalDate lastAccessedDate,
                                              Boolean hasCustomAlias, Boolean hasExpired, Pageable pageable);
}
