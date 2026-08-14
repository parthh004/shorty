package com.tss.shorty.security;

import com.tss.shorty.repository.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, TokenBlacklistRepository tokenBlacklistRepository, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver)
    {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        try {

            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token))
            {
                // 1. Validate the token (This will throw exceptions if expired/invalid)
                jwtTokenProvider.validateToken(token);

                // 2. Check if the token is Blacklisted (Logged out)
                String tokenId = jwtTokenProvider.getTokenIdFromToken(token);

                if (tokenBlacklistRepository.existsById(tokenId))
                {
                    throw new BadCredentialsException("Token has been invalidated (Logged out)");
                }

                // 3. Extract email and load user
                String email = jwtTokenProvider.getEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (!userDetails.isEnabled())
                {
                    throw new BadCredentialsException("Your account has been blocked by the administrator.");
                }

                // 4. Set Authentication in Security Context
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            // Continue the filter chain if everything is fine
            filterChain.doFilter(request, response);

        }
        catch (Exception ex)
        {
            // If ANY exception happens (like ExpiredJwtException or Blacklisted token),
            // clear the context and send the error directly to the GlobalExceptionHandler!
            SecurityContextHolder.clearContext();
            resolver.resolveException(request, response, null, ex);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request)
    {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        return null;
    }
}
