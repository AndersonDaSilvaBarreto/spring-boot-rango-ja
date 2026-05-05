package com.especial_topics_1.restaurant.auth;

import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthenticatedUserService {
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || Objects.equals(authentication.getPrincipal(), "anonymousUser")) {
            throw new BusinessException("Usuário não autenticado ou sessão expirada.");
        }

        return (User) authentication.getPrincipal();
    }
    }

