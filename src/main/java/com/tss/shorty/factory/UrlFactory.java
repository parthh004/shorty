package com.tss.shorty.factory;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import com.tss.shorty.service.ConfigService;
import com.tss.shorty.service.ShortUrlGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UrlFactory {
    private final ConfigService configService; // to fetch the system config values like visitLimit
    private final ShortUrlGenerator urlGenerator; // for generating url of 7 length using base62 encoder

    public UrlFactory(ConfigService configService, ShortUrlGenerator urlGenerator) {
        this.configService = configService;
        this.urlGenerator = urlGenerator;
    }

    public Url createAliasUrl(User user, String originalUrl, String alias) {
        return buildUrl(user, originalUrl, alias, alias);
    }

    public Url createGeneratedUrl(User user, String originalUrl) {
        String generatedUrl = urlGenerator.generateUniqueCode();
        return buildUrl(user, originalUrl, generatedUrl, null);
    }

    private Url buildUrl(User user, String originalUrl, String shortUrl, String alias) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setUser(user);
        url.setShortUrl(shortUrl);
        if (alias != null && !alias.isBlank()) {
            url.setCustomAlias(true);
        }
        int maxVisits = configService.getIntegerConfig("MAX_VISITS_FREE_URL");
        int expiryLimit = configService.getIntegerConfig("MAX_VISITS_FREE_URL");

        url.setVisitLimit(maxVisits);
        url.setRemainingVisits(maxVisits);
        url.setTotalVisits(0);

        LocalDateTime dateTime = LocalDateTime.now();
        url.setLastAccessedOn(dateTime);
        url.setExpiryDate(dateTime.plusDays(expiryLimit));

        return url;
    }
}
