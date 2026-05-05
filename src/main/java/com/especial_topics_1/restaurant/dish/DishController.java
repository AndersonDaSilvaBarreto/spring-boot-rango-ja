package com.especial_topics_1.restaurant.dish;

import com.especial_topics_1.restaurant.dish.dto.request.RegisterDishRequest;
import com.especial_topics_1.restaurant.dish.dto.request.UpdateDishRequest;
import com.especial_topics_1.restaurant.dish.dto.response.DishResponse;
import com.especial_topics_1.restaurant.standard.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/restaurants/{restaurantId}/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<DishResponse>> createDish(
            @PathVariable UUID restaurantId,
            @Valid @ModelAttribute RegisterDishRequest request)
    {
        StandardResponse<DishResponse> response = StandardResponse.success(
                dishService.create(restaurantId, request),
                "Dish Created"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<DishResponse>> findById( @PathVariable UUID restaurantId,@PathVariable UUID id) {
        StandardResponse<DishResponse>  response = StandardResponse.success(
                dishService.findById(restaurantId, id),
                "Dish found"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping()
    public ResponseEntity<StandardResponse<Page<DishResponse>>> findAllByRestaurantId(
            @PathVariable UUID restaurantId,
            @PageableDefault(size = 12,sort = "name") Pageable pageable
    ) {
        StandardResponse<Page<DishResponse>> response = StandardResponse.success(
                dishService.findAllByRestaurantId(restaurantId,pageable),
                "Restaurant dish list"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping(value = "/{dishId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<DishResponse>> updateDish(
            @PathVariable UUID restaurantId,
            @PathVariable UUID dishId,
            @Valid @ModelAttribute UpdateDishRequest request) {

        StandardResponse<DishResponse> response = StandardResponse.success(
                dishService.updateDish(restaurantId, dishId, request),
                "Dish updated"
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteById(
            @PathVariable UUID restaurantId,
            @PathVariable UUID id) {
        dishService.deleteById(restaurantId,id);
        StandardResponse<Void> response = StandardResponse.success(
                null,
                "Dish deleted"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
