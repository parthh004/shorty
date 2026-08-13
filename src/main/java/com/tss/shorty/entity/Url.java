package com.tss.shorty.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "urls")
@Getter
@Setter
@NoArgsConstructor
public class Url extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "url_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private boolean customAlias = false;

    @Column(nullable = false)
    private Integer visitLimit;

    @Column(nullable = false)
    private Integer remainingVisits;

    @Column(nullable = false)
    private Integer totalVisits;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime lastAccessedOn;

    @Column(nullable = false)
    private boolean isExpired = false;

    @Column(nullable = false)
    private boolean isExpiryNotified = false;

    public Url(User user, String shortUrl, String originalUrl) {
        this.user = user;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
    }
}
