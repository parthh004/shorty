package com.tss.shorty.service;

import com.tss.shorty.entity.Url;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.mapper.CachedUrlMapper;
import com.tss.shorty.payload.response.UrlCacheResponseDto;
import com.tss.shorty.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UrlCacheService {
    private static final Logger log = LoggerFactory.getLogger(UrlCacheService.class);
    private final UrlRepository urlRepository;
    private final CachedUrlMapper cachedUrlMapper;

    public UrlCacheService(UrlRepository urlRepository, CachedUrlMapper cachedUrlMapper) {
        this.urlRepository = urlRepository;
        this.cachedUrlMapper = cachedUrlMapper;
    }

    @Cacheable(value = "urls", key = "#shortCode")
    public UrlCacheResponseDto getCachedUrl(String shortCode) {
        try {
            Thread.sleep(5000);
            log.info("---- THREAD SLEPT FOR 5 SECONDS ----");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Url url = urlRepository.findByShortUrlAndIsActiveTrue(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("URL with shortCode:" + shortCode + " doesn't exists"));

        return cachedUrlMapper.mapToCachedUrl(url);
    }
}
