package com.especial_topics_1.restaurant.user;

import com.especial_topics_1.restaurant.auth.AuthenticatedUserService;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
import com.especial_topics_1.restaurant.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public UserResponse me() {
        User loggedUser = authenticatedUserService.getCurrentUser();

        User user = userRepository.findById(loggedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return new UserResponse(user);
    }
}
