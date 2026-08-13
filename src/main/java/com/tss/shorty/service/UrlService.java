package com.tss.shorty.service;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.exception.ResourceAlreadyExistsException;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.exception.UrlApiException;
import com.tss.shorty.mapper.PageMapper;
import com.tss.shorty.mapper.UrlMapper;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.request.UrlUpdateDto;
import com.tss.shorty.payload.response.PaginatedDto;
import com.tss.shorty.payload.response.UrlAliasCheckResponseDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import com.tss.shorty.repository.UrlRepository;
import com.tss.shorty.util.UrlSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UrlService implements IUrlService {
    private final UrlRepository urlRepository;
    private final UrlFactory urlFactory;
    private final UrlMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    public UrlService(UrlRepository urlRepository, UrlFactory urlFactory, UrlMapper mapper) {
        this.urlRepository = urlRepository;
        this.urlFactory = urlFactory;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UrlResponseDto createUrl(UrlRequestDto requestDto, User user) {
        Url url;
        String originalUrl = requestDto.getOriginalUrl();
        String alias = requestDto.getAlias();

        if (user.getAvailableSlots() <= 0) {
            throw new UrlApiException("slot quota is exhausted for user " + user);
        }
        if (urlRepository.existsByOriginalUrlAndUser(originalUrl, user)) {
            throw new ResourceAlreadyExistsException("Original url already exists url:" + originalUrl);
        }
        user.setAvailableSlots(user.getAvailableSlots() - 1);
        if (requestDto.getAlias() == null || requestDto.getAlias().isBlank()) {
            url = urlFactory.createGeneratedUrl(user, originalUrl);
            Url savedUrl = urlRepository.save(url);

            return mapper.mapToUrlCreateResponse(savedUrl);
        } else {
            if (!urlRepository.existsByShortUrl(alias)) {
                url = urlFactory.createAliasUrl(user, originalUrl, alias);
                Url savedUrl = urlRepository.save(url);

                return mapper.mapToUrlCreateResponse(savedUrl);
            }
            throw new ResourceAlreadyExistsException("url with same alias exists");
        }
    }

    @Override
    public UrlDetailResponseDto getUrlDetails(UUID urlId, User user) {
        Url url = urlRepository.findByIdAndIsActiveTrueAndUser(urlId, user).orElseThrow(() -> new ResourceNotFoundException("url with id:" + urlId + " doesn't exists for user with id:" + user.getUserId()));

        return mapper.mapToUrlDetails(url);
    }

    @Transactional
    @Override
    public void updateUrlDetails(User user, UrlUpdateDto updateDto, UUID urlId) {
        Url url = urlRepository.findByIdAndIsActiveTrueAndUser(urlId, user).orElseThrow(() -> new ResourceNotFoundException("url with id:" + urlId + " doesn't exists for user with id:" + user.getUserId()));

        String updateUrl = updateDto.getOriginalUrl();
        url.setOriginalUrl(updateUrl);

        urlRepository.save(url);
    }

    @Override
    public void deleteUrlDetails(User user, UUID urlId) {
        Url url = urlRepository.findByIdAndIsActiveTrueAndUser(urlId, user).orElseThrow(() -> new ResourceNotFoundException("url with id:" + urlId + " doesn't exists for user with id:" + user.getUserId()));

        url.setActive(false);
        urlRepository.save(url);
    }

    @Override
    public UrlAliasCheckResponseDto checkAliasAvailable(String alias) {
        Boolean available = urlRepository.existsByShortUrl(alias);
        return mapper.mapToAliasCheckResponse(alias, available);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void reinstateUrl(UUID id) {
        Url url = urlRepository.findByIdAndIsActiveFalse(id).orElseThrow(() -> new ResourceNotFoundException("url with id:" + id + " doesn't exists"));

        url.setActive(true);
        log.info("url reinstated for id:{}", id);
        urlRepository.save(url);
    }

    @Override
    @Transactional
    public String getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findValidUrlForRedirect(shortUrl, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("url:" + shortUrl + " doesn't exists"));

        if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This URL has expired.");
        }
        if (url.getRemainingVisits() <= 0) {
            throw new IllegalArgumentException("This URL has reached its maximum visit limit.");
        }

        url.setTotalVisits(url.getTotalVisits() + 1);
        url.setRemainingVisits(url.getRemainingVisits() - 1);
        url.setLastAccessedOn(LocalDateTime.now());

        urlRepository.save(url);
        return url.getOriginalUrl();
    }

    @Override
    public PaginatedDto<UrlDetailResponseDto> getAll(
            User currentUser, LocalDate expiryDate,
            LocalDate lastAccessedDate, Boolean hasCustomAlias,
            Boolean hasExpired, Pageable pageable) {

        Specification<Url> specification = Specification
                .where(UrlSpecification.isActive())
                .and(UrlSpecification.expiresOnDate(expiryDate))
                .and(UrlSpecification.accessedFromDateToNow(lastAccessedDate))
                .and(UrlSpecification.hasCustomAlias(hasCustomAlias))
                .and(UrlSpecification.hasExpired(hasExpired)
                );

        Page<Url> page = urlRepository.findAll(specification, pageable);
        return PageMapper.toPaginatedDto(page.map(mapper::mapToUrlDetails));
    }

}
