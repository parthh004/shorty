package com.tss.shorty.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider
{

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication)
    {
        String email = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // ADDED: Unique Token ID for Blacklisting!
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .claim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .signWith(key())
                .compact();
    }

    private SecretKey key()
    {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String token)
    {
        try
        {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
            return true;
        }
        catch (MalformedJwtException ex)
        {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new BadCredentialsException("Invalid JWT token");
        }
        catch (ExpiredJwtException ex)
        {
            log.error("Expired JWT token: {}", ex.getMessage());
            throw new BadCredentialsException("Expired JWT token");
        }
        catch (UnsupportedJwtException ex)
        {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new BadCredentialsException("Unsupported JWT token");
        }
        catch (IllegalArgumentException ex)
        {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new BadCredentialsException("JWT claims string is empty");
        }
        catch (Exception ex)
        {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new BadCredentialsException("Token validation failed");
        }
    }

    // Renamed slightly for clarity, since the 'subject' is the email
    public String getEmailFromToken(String token)
    {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public String getTokenIdFromToken(String token)
    {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getId();
    }

    public LocalDateTime getExpirationDateFromToken(String token)
    {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}