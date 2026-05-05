package com.especial_topics_1.restaurant.auth;

import com.especial_topics_1.restaurant.auth.dto.request.LoginRequest;
import com.especial_topics_1.restaurant.auth.dto.request.RegisterUserRequest;
import com.especial_topics_1.restaurant.auth.dto.request.VerifyCodeRequest;
import com.especial_topics_1.restaurant.auth.dto.response.TokenResponse;
import com.especial_topics_1.restaurant.user.User;
import com.especial_topics_1.restaurant.auth.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
    

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> initiateRegistration(@RequestBody @Valid RegisterUserRequest request) {
        authService.startRegistration(request);
        return ResponseEntity.ok("Código de verificação enviado para " + request.email());
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody @Valid VerifyCodeRequest request) {
        authService.confirmRegistration(request);
        return ResponseEntity.ok("Cadastro confirmado com sucesso!.");
    }


    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.resendVerificationCode(email);
        return ResponseEntity.ok("Novo código enviado para " + email);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse tokens = authService.authenticate(request);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.accessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Login realizado com sucesso!");

    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(name = "refreshToken",required = false) String oldRefreshToken) {
        if (oldRefreshToken == null) {
            return ResponseEntity.status(401).body("Refresh Token ausente.");
        }
        var tokens = authService.refreshAccessToken(oldRefreshToken);

        ResponseCookie newAccessCookie = ResponseCookie.from("accessToken", tokens.accessToken())
                .httpOnly(true)
                .secure(false) // Lembre de mudar para true quando tiver HTTPS
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Strict")
                .build();

        // Monta o novo Cookie do Refresh Token (Renovando os 7 dias)
        ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
                .body("Sessão renovada com sucesso! O ciclo recomeçou.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie accessToken = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .body("Logout realizado com sucesso.");
    }
}
