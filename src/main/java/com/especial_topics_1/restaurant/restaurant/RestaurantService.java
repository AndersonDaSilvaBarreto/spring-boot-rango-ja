package com.especial_topics_1.restaurant.restaurant;

import com.especial_topics_1.restaurant.auth.AuthenticatedUserService;
import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
import com.especial_topics_1.restaurant.restaurant.dto.request.RegisterRestaurantRequest;
import com.especial_topics_1.restaurant.restaurant.dto.request.UpdateRestaurantRequest;
import com.especial_topics_1.restaurant.restaurant.dto.response.RestaurantResponse;
import com.especial_topics_1.restaurant.storage.StorageService;
import com.especial_topics_1.restaurant.user.Role;
import com.especial_topics_1.restaurant.user.User;
import com.especial_topics_1.restaurant.util.ImageProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final ImageProcessorService imageProcessorService;
    private final StorageService storageService;

    @Transactional
    public RestaurantResponse create(RegisterRestaurantRequest dto) {
        User loggedUser = authenticatedUserService.getCurrentUser();


        String imageUrl = null;
        if(dto.logo() != null && !dto.logo().isEmpty()) {
            imageUrl = imageProcessorService.processAndUpload(dto.logo(), 300,300);
        }

        Restaurant newRestaurant = Restaurant.builder()
                .name(dto.name())
                .description(dto.description())
                .logoUrl(imageUrl)
                .owner(loggedUser)
                .build();

        Restaurant createdRestaurant = restaurantRepository.save(newRestaurant);
        return new RestaurantResponse(createdRestaurant);
    }

    public Page<RestaurantResponse> findAll(Pageable pageable) {
        Page<Restaurant> restaurants = restaurantRepository.findAll(pageable);
        return restaurants.map(RestaurantResponse::new);
    }
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getMyRestaurants(Pageable pageable) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        Page<Restaurant> restaurants = restaurantRepository.findAllByOwnerId(loggedUser.getId(), pageable);

        return restaurants.map(RestaurantResponse::new);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse findById(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!"));
        return new RestaurantResponse(restaurant);
    }

    @Transactional
    public RestaurantResponse update(UUID id, UpdateRestaurantRequest dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!"));

        User loggedUser = authenticatedUserService.getCurrentUser();

        if (!loggedUser.hasRole(Role.ADMIN) && !loggedUser.equals(restaurant.getOwner())) {
            throw new BusinessException("Você não tem permissão para alterar os dados do banco!");
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            restaurant.setName(dto.name());
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            restaurant.setDescription(dto.description());
        }

        if(dto.isOpen() != null) {
            restaurant.setIsOpen(dto.isOpen());
        }

        if (dto.logo() != null && !dto.logo().isEmpty()) {
            if(restaurant.getLogoUrl() != null) {
                storageService.deleteImage(restaurant.getLogoUrl());
            }
            String newLogoUrl = this.imageProcessorService.processAndUpload(dto.logo(),300,300);
            restaurant.setLogoUrl(newLogoUrl);
        }
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponse(updatedRestaurant);
    }

    @Transactional
    public void deleteById(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!"));
        User loggedUser = authenticatedUserService.getCurrentUser();
        if (!loggedUser.hasRole(Role.ADMIN) && !loggedUser.equals(restaurant.getOwner())) {
            throw new BusinessException("Você não tem permissão para deletar o restaurante!");
        }

        restaurantRepository.delete(restaurant);
    }


}
