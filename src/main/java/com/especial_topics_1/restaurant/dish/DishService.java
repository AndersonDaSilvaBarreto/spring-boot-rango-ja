package com.especial_topics_1.restaurant.dish;

import com.especial_topics_1.restaurant.auth.AuthenticatedUserService;
import com.especial_topics_1.restaurant.category.Category;
import com.especial_topics_1.restaurant.category.CategoryRepository;
import com.especial_topics_1.restaurant.dish.dto.request.RegisterDishRequest;
import com.especial_topics_1.restaurant.dish.dto.request.UpdateDishRequest;
import com.especial_topics_1.restaurant.dish.dto.response.DishResponse;
import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
import com.especial_topics_1.restaurant.restaurant.Restaurant;
import com.especial_topics_1.restaurant.restaurant.RestaurantRepository;
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
public class DishService {
    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final AuthenticatedUserService authenticatedUserService;
    private final ImageProcessorService imageProcessorService;

    @Transactional
    public DishResponse create(UUID restaurantId, RegisterDishRequest request) {

        User loggedUser = authenticatedUserService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado"));
        if(!loggedUser.equals(restaurant.getOwner())){
            throw new BusinessException("Você não tem permissão para adicionar um prato nesse restaurante!");
        }
        String imageUrl = null;

        if(request.image() != null && !request.image().isEmpty())
            imageUrl = this.imageProcessorService.processAndUpload(request.image(), 600, 600);

        Dish dish = Dish.builder()
                .name(request.name())
                .price(request.price())
                .restaurant(restaurant)
                .category(categoryRepository.getReferenceById(request.categoryId()))
                .imageUrl(imageUrl)
                .build();

        dishRepository.save(dish);

        return new DishResponse(dish);
    }

    @Transactional(readOnly = true)
    public DishResponse findById(UUID restaurantId,UUID id) {
        Dish dish = dishRepository.findByRestaurantIdAndId(restaurantId,id)
                .orElseThrow(() -> new ResourceNotFoundException("Prato não encontrado!"));
        return new DishResponse(dish);

    }

    @Transactional(readOnly = true)
    public Page<DishResponse> findAllByRestaurantId(UUID restaurantId, Pageable pageable) {
        Page<Dish> dishes = dishRepository.findAllByRestaurantId(restaurantId,pageable);
        return dishes.map(DishResponse::new);
    }

    @Transactional
    public DishResponse updateDish(UUID restaurantId, UUID dishId, UpdateDishRequest request) {
        User loggedUser = authenticatedUserService.getCurrentUser();

        Dish dish = dishRepository.findByRestaurantIdAndId(restaurantId,dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Prato não encontrado!"));


        if (!loggedUser.hasRole(Role.ADMIN) && !loggedUser.equals(dish.getRestaurant().getOwner())) {
            throw new BusinessException("Você não tem permissão para editar este prato!");
        }



       if(request.name() != null && !request.name().trim().isEmpty())  dish.setName(request.name());
       if(request.price() != null)  dish.setPrice(request.price());
       if(request.categoryId() != null) {
           Category category = categoryRepository.findById(request.categoryId())
                   .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!")
           );
           dish.setCategory(category);
       }

        if (request.image() != null && !request.image().isEmpty()) {

            if (dish.getImageUrl() != null) {
                storageService.deleteImage(dish.getImageUrl());
            }

            String newImageUrl = this.imageProcessorService.processAndUpload(request.image(),600,600);
            dish.setImageUrl(newImageUrl);
        }

        dishRepository.save(dish);
        return new DishResponse(dish);
    }

    @Transactional
    public void deleteById(UUID restaurantId,UUID id) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        Dish dish = dishRepository.findByRestaurantIdAndId(restaurantId,id)
                .orElseThrow(() -> new ResourceNotFoundException("Prato não encontrado!"));
        if (!loggedUser.hasRole(Role.ADMIN)
                && !loggedUser.equals(dish.getRestaurant().getOwner())) {
            throw new BusinessException("Você não tem permissão para deletar este prato!");
        }
        dishRepository.delete(dish);
    }

}
