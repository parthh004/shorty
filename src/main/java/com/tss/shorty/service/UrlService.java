package com.tss.shorty.service;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.exception.ResourceAlreadyExistsException;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.exception.UrlSlotException;
import com.tss.shorty.mapper.UrlMapper;
import com.tss.shorty.payload.request.UrlRequestDto;
import com.tss.shorty.payload.response.UrlDetailResponseDto;
import com.tss.shorty.payload.response.UrlResponseDto;
import com.tss.shorty.repository.IUserRepository;
import com.tss.shorty.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UrlService implements IUrlService {
    private final UrlRepository urlRepository;
    private final UrlFactory urlFactory;
    private final UrlMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    public UrlService(UrlRepository urlRepository, UrlFactory urlFactory,
                      UrlMapper mapper) {
        this.urlRepository = urlRepository;
        this.urlFactory = urlFactory;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UrlResponseDto createUrl(UrlRequestDto requestDto, User user) {
        Url url;
        String originalUrl = requestDto.getOriginalUrl();
        String alias = requestDto.getCustomAlias();

        if (user.getAvailableSlots() <= 0) {
            throw new UrlSlotException("slot quota is exhausted for user " + user);
        }

        user.setAvailableSlots(user.getAvailableSlots() - 1);
        if (requestDto.getCustomAlias() == null || requestDto.getCustomAlias().isBlank()) {
            url = urlFactory.createGeneratedUrl(user, originalUrl);
            Url savedUrl = urlRepository.save(url);

            return mapper.mapToUrlCreateResponse(savedUrl);
        } else {
            if (!urlRepository.existsByCustomerAlias(alias)) {
                url = urlFactory.createAliasUrl(user, originalUrl, alias);
                Url savedUrl = urlRepository.save(url);

                return mapper.mapToUrlCreateResponse(savedUrl);
            }
            throw new ResourceAlreadyExistsException("url with same alias exists");
        }
    }

    @Override
    public UrlDetailResponseDto getUrlDetails(UUID urlId, User user) {
        Url url = urlRepository.findByIdAndIsActiveTrueAndUser(urlId, user)
                .orElseThrow(() -> new ResourceNotFoundException("url with id:" + urlId +
                        " doesn't exists for user with id:" + user.getUserId()));

        return mapper.mapToUrlDetails(url);
    }


}
