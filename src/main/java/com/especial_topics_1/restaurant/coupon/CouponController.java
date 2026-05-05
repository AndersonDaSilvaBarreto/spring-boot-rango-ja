package com.especial_topics_1.restaurant.coupon;

import com.especial_topics_1.restaurant.coupon.dto.request.CreateGlobalCouponRequest;
import com.especial_topics_1.restaurant.coupon.dto.request.CreateRestaurantCouponRequest;
import com.especial_topics_1.restaurant.coupon.dto.response.CouponResponse;
import com.especial_topics_1.restaurant.coupon.dto.response.FindCouponsByNameAndRestaurantId;
import com.especial_topics_1.restaurant.standard.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/restaurants")
    public ResponseEntity<StandardResponse<CouponResponse>> createRestaurantCoupon(
            @Valid @RequestBody CreateRestaurantCouponRequest dto
    ) {
        StandardResponse<CouponResponse> response = StandardResponse.created(
                couponService.createRestaurantCoupon(dto),
                "Restaurant coupon created"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/global")
    public ResponseEntity<StandardResponse<CouponResponse>> createGlobalCoupon(
            @Valid @RequestBody CreateGlobalCouponRequest dto
    ) {

        StandardResponse<CouponResponse> response = StandardResponse.created(
                couponService.createGlobalCoupon(dto),
                "Restaurant coupon created"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<StandardResponse<FindCouponsByNameAndRestaurantId>> findByNameAndRestaurantId(
            @RequestParam("name") String name,
            @RequestParam("restaurantId") UUID restaurantId) {

        StandardResponse<FindCouponsByNameAndRestaurantId> response = StandardResponse.success(
                couponService.findValidCouponsByNameAndRestaurantId(name, restaurantId),
                "Object with counts"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/search/restaurants")
    public ResponseEntity<StandardResponse<Page<CouponResponse>>>
    findValidCouponsByRestaurantIdWithGlobalCoupons(
            @RequestParam("restaurantId") UUID restaurantId,
            @PageableDefault(
                    size = 12,
                    sort = {"name", "createdAt"},
                    direction = Sort.Direction.DESC) Pageable pageable

    ) {
        StandardResponse<Page<CouponResponse>> response = StandardResponse.success(
                couponService.findValidCouponsByRestaurantIdWithGlobalCoupons(
                        restaurantId,
                        pageable
                ),
                "Coupons list"
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StandardResponse<CouponResponse>> invalidateCount(
            @PathVariable UUID id
    ) {
        StandardResponse<CouponResponse> response = StandardResponse.success(
                couponService.invalidateCoupon(id),
                "Coupon invalidated!"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
