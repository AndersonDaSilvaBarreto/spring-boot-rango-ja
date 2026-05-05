package com.especial_topics_1.restaurant.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.especial_topics_1.restaurant.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private static final int EXPIRATION_MINUTES = 15;

    public String generateToken (User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("restaurant-api")
                    .withSubject(user.getId().toString())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public DecodedJWT getDecodedToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("restaurant-api")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            // Se o token estiver vencido ou for falso, retorna nulo para o filtro barrar
            return null;
        }
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES).toInstant(ZoneOffset.of("-03:00"));
    }
}
