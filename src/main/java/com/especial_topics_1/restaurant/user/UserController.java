package com.especial_topics_1.restaurant.user;

import com.especial_topics_1.restaurant.standard.StandardResponse;
import com.especial_topics_1.restaurant.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<UserResponse>> me() {
        StandardResponse<UserResponse> response = StandardResponse.success(
                userService.me(),
                
                "Usuário logado"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
