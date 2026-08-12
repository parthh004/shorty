package com.tss.shorty.service;

import com.tss.shorty.exception.ShortCodeGenerationException;
import com.tss.shorty.repository.UrlRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortUrlGenerator {
    private static final String ALPHABET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_RETRIES = 5;

    private final SecureRandom random = new SecureRandom();
    private final UrlRepository urlRepository;

    public ShortUrlGenerator(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String url = randomCode();
            if (!urlRepository.existsByShortUrl(url)) {
                return url;
            }
        }
        throw new ShortCodeGenerationException("Could not generate a unique code after retries");
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
