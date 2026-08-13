package com.tss.shorty.repository;

import com.tss.shorty.entity.Url;
import com.tss.shorty.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<Url, UUID>, JpaSpecificationExecutor<Url> {

    Boolean existsByShortUrl(String shortUrl);

    Boolean existsByOriginalUrlAndUser(String customAlias, User user);

    Optional<Url> findByIdAndIsActiveTrue(UUID id);

    Optional<Url> findByIdAndIsActiveFalse(UUID id);

    Page<Url> findByIsActiveTrue(Pageable pageable);

    Page<Url> findAllByIdAndIsActiveTrue(UUID id, Pageable pageable);

    List<Url> findAllByIdAndIsActiveTrue(UUID id);

    Optional<Url> findByIdAndIsActiveTrueAndUser(UUID id, User user);

    Optional<Url> findByIdAndUser(UUID id, User user);

    Optional<Url> findByShortUrlAndIsActiveTrue(String shortUrl);

    @Query("SELECT u FROM Url u WHERE u.isExpiryNotified = false " +
            "AND (u.remainingVisits <= 0 OR u.expiryDate < :now)")
    List<Url> findUrlsRequiringExpiryNotification(@Param("now") LocalDateTime now);

    @Query("SELECT u FROM Url u " +
            "WHERE u.shortUrl = :shortUrl " +
            "AND u.isActive = true " +
            "AND u.isExpired = false " +
            "AND u.remainingVisits > 0 " +
            "AND u.expiryDate > :now")
    Optional<Url> findValidUrlForRedirect(@Param("shortUrl") String shortUrl,
                                          @Param("now") LocalDateTime now);
}
