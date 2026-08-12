package com.tss.shorty.service;

import com.tss.shorty.entity.User;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;

import java.util.UUID;

public interface IUrlService {
    UrlResponseDto createUrl(UrlRequestDto requestDto, User user);

    UrlDetailResponseDto getUrlDetails(UUID urlId, User user);
}
