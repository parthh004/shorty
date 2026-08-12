package com.tss.shorty.repository;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID> {
    Boolean existsByCustomerAlias(String customerAlias);

    Boolean existsByShortUrl(String shortUrl);

    Boolean existsByOriginalUrl(String customerAlias);

    Optional<Url> findByIdAndIsActiveTrue(UUID id);

    Page<Url> findByIsActiveTrue(Pageable pageable);

    Page<Url> findAllByIdAndIsActiveTrue(UUID id, Pageable pageable);

    List<Url> findAllByIdAndIsActiveTrue(UUID id);

    Optional<Url> findByIdAndIsActiveTrueAndUser(UUID id, User user);
}
