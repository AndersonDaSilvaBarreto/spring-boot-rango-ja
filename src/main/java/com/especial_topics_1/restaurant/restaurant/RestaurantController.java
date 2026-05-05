package com.especial_topics_1.restaurant.restaurant;

import com.especial_topics_1.restaurant.restaurant.dto.request.RegisterRestaurantRequest;
import com.especial_topics_1.restaurant.restaurant.dto.request.UpdateRestaurantRequest;
import com.especial_topics_1.restaurant.restaurant.dto.response.RestaurantResponse;
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
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<RestaurantResponse>> create(
            @Valid @ModelAttribute RegisterRestaurantRequest request) {
        RestaurantResponse restaurantResponse = restaurantService.create(request);
        StandardResponse<RestaurantResponse> response = StandardResponse.created(
                        restaurantResponse,
                        "Restaurant created");


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public  ResponseEntity<StandardResponse<RestaurantResponse>> findById(@PathVariable UUID id) {
        StandardResponse<RestaurantResponse> response = StandardResponse.success(
                restaurantService.findById(id),
                "Restaurant find"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping
    public ResponseEntity<StandardResponse<Page<RestaurantResponse>>> findAll(
            @PageableDefault(size = 12,sort = "name")Pageable pageable
            ){
        StandardResponse<Page<RestaurantResponse>> response = StandardResponse.success(
                restaurantService.findAll(pageable),
                "Restaurant list");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<Page<RestaurantResponse>>> getMyRestaurants(
            @PageableDefault(size = 12,sort = "name")Pageable pageable
    ) {
        StandardResponse<Page<RestaurantResponse>> response = StandardResponse.success(
                restaurantService.getMyRestaurants(pageable),
                "My restaurants list"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<RestaurantResponse>> updateRestaurant(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateRestaurantRequest dto) {

        StandardResponse<RestaurantResponse> response = StandardResponse.success(
                restaurantService.update(id, dto),
                "Restaurant updated"
        );

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteRestaurant(
            @PathVariable UUID id) {
        restaurantService.deleteById(id);
        StandardResponse<Void> response = StandardResponse.success(null, "restaurante deleted");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
