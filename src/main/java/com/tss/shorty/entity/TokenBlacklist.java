package com.tss.shorty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklist
{
    @Id
    @Column(nullable = false, unique = true)
    private String tokenId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}