package com.especial_topics_1.restaurant.auth;

import com.especial_topics_1.restaurant.auth.dto.request.LoginRequest;
import com.especial_topics_1.restaurant.auth.dto.request.RegisterUserRequest;
import com.especial_topics_1.restaurant.auth.dto.request.VerifyCodeRequest;
import com.especial_topics_1.restaurant.auth.dto.response.TokenResponse;
import com.especial_topics_1.restaurant.auth.record.PendingUser;
import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
import com.especial_topics_1.restaurant.user.User;
import com.especial_topics_1.restaurant.user.UserRepository;
import com.especial_topics_1.restaurant.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int REFRESH_TOKEN_DAYS = 7;

    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public void startRegistration(RegisterUserRequest dto) {
        if(userRepository.findUserByEmail(dto.email()).isPresent()) {
            throw new ResourceNotFoundException("E-mail já cadastrado no sistema.");
        }
        try {
            String code = String.format("%06d", new Random().nextInt(1000000));
            String hashedPassword = passwordEncoder.encode(dto.password());

            PendingUser tempUser = new PendingUser(
                    dto.name(),
                    dto.email(),
                    hashedPassword,
                    dto.phone(),
                    code);
            String json = objectMapper.writeValueAsString(tempUser);

            redisTemplate.opsForValue().set("reg:" + dto.email(), json
            ,5, TimeUnit.MINUTES);
            emailService.sendVerificationCode(dto.email(), code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void confirmRegistration(VerifyCodeRequest dto) {
        String json = redisTemplate.opsForValue().get("reg:" + dto.email());
        if (json == null) {
            throw new BusinessException("Código expirado ou e-mail não encontrado. Tente se cadastrar novamente.");
        }
        try {
            PendingUser pendingUser = objectMapper.readValue(json, PendingUser.class);
            if (pendingUser.code().equals(dto.code())) {
                User newUser = User.builder()
                        .name(pendingUser.name())
                        .email(pendingUser.email())
                        .passwordHash(pendingUser.passwordHash())
                        .phone(pendingUser.phone()).build();
                redisTemplate.delete("reg:" + dto.email());
                userRepository.save(newUser);
            }else {
                throw new BusinessException("Código de verificação incorreto.");
            }
        } catch (JacksonException e) {
            throw new BusinessException("Erro na validação do código.");
        }
    }

    public void resendVerificationCode(String email) {
        try {
            String key = "reg:" + email;
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                throw new ResourceNotFoundException("Cadastro não encontrado ou expirado.");
            }

            PendingUser tempUser = objectMapper.readValue(json, PendingUser.class);

            String newCode = String.format("%06d", new Random().nextInt(1000000));

            PendingUser updatedUser = new PendingUser(
                    tempUser.name(),
                    tempUser.email(),
                    tempUser.passwordHash(),
                    tempUser.phone(),
                    newCode
            );

            String updatedJson = objectMapper.writeValueAsString(updatedUser);

            redisTemplate.opsForValue().set(key, updatedJson, 15, TimeUnit.MINUTES);

            emailService.sendVerificationCode(email, newCode);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public TokenResponse authenticate(LoginRequest dto) {
        User user = userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha incorretos"));
        if(!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new BusinessException("E-mail ou senha incorretos");
        }
        String accessToken = tokenService.generateToken(user);
        String refreshToken = tokenService.generateRefreshToken();

        redisTemplate.opsForValue().set("refresh:" + refreshToken, user.getId().toString(), REFRESH_TOKEN_DAYS, TimeUnit.DAYS);

         return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public TokenResponse refreshAccessToken(String oldRefreshToken) {
        String userId = redisTemplate.opsForValue().get("refresh:" + oldRefreshToken);

        if(userId == null) {
            throw new RuntimeException("Refresh Token inválido ou expirado. Faça login novamente.");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        redisTemplate.delete("refresh:" + oldRefreshToken);

        String newAccessToken = tokenService.generateToken(user);
        String newRefreshToken = tokenService.generateRefreshToken();

        redisTemplate.opsForValue().set(
                "refresh:" + newRefreshToken
                , user.getId().toString()
                , REFRESH_TOKEN_DAYS
                , TimeUnit.DAYS);

        return new TokenResponse(newAccessToken,newRefreshToken);

    }

}
