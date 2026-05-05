package com.especial_topics_1.restaurant.auth;

import com.especial_topics_1.restaurant.user.Role;
import com.especial_topics_1.restaurant.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        authenticateUserFromToken(request);

        filterChain.doFilter(request, response);
    }

    private void authenticateUserFromToken(HttpServletRequest request) {
        String token = this.recoverToken(request);
        if (token == null) return;

        var decodedJWT = tokenService.getDecodedToken(token);
        if (decodedJWT == null) return;

        String userId = decodedJWT.getSubject();
        String role = decodedJWT.getClaim("role").asString();

        User user = User.builder()
                .id(UUID.fromString(userId))
                .role(Role.valueOf(role))
                .build();
        var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Se chegou até aqui, é porque o caminho feliz aconteceu

    }

    private String recoverToken(HttpServletRequest request) {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;

    }
}


