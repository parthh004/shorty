package com.tss.shorty.service;

import com.tss.shorty.entity.User;
import com.tss.shorty.exception.ResourceNotFoundException;
import com.tss.shorty.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
public class CurrentUserProvider {
    private final UserRepository userRepository;
    private User cachedUser;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        if (cachedUser == null) {
            String email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            cachedUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User with email:" + email + " doesn't exists/has been blocked by admin"));
        }
        return cachedUser;
    }
}
