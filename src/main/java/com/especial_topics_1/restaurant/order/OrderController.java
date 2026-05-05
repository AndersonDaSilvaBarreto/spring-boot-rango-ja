package com.especial_topics_1.restaurant.order;

import com.especial_topics_1.restaurant.order.dto.request.RegisterOrderRequest;
import com.especial_topics_1.restaurant.order.dto.request.UpdateOrderStatusRequest;
import com.especial_topics_1.restaurant.order.dto.response.OrderDetailResponse;
import com.especial_topics_1.restaurant.order.dto.response.RegisterOrderResponse;
import com.especial_topics_1.restaurant.order.dto.response.RestaurantOrderSummaryResponse;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<StandardResponse<RegisterOrderResponse>> create(
            @RequestBody @Valid RegisterOrderRequest request
            ) {
            StandardResponse<RegisterOrderResponse>  response= StandardResponse.created(
            orderService.create(request),
                "Order created"
        );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<OrderDetailResponse>> getOrderDetail(
            @PathVariable UUID id
    ) {
        StandardResponse<OrderDetailResponse> response = StandardResponse.success(
                orderService.getOrderDetailById(id),
                "Order detail"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping("/me")
    public ResponseEntity<StandardResponse<Page<OrderDetailResponse>>> getMyOrders(
            @PageableDefault(size = 12,sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        StandardResponse<Page<OrderDetailResponse>> response = StandardResponse.success(
                orderService.getMyOrders(pageable),
                "Logged user's orders"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<StandardResponse<Page<OrderDetailResponse>>> getRestaurantOrders(
            @PathVariable UUID restaurantId,
            @PageableDefault(
                    size = 12,
                    sort = {"status", "createdAt"},
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        StandardResponse<Page<OrderDetailResponse>> response = StandardResponse.success(
                orderService.getRestaurantOrders(restaurantId, pageable),
                "Restaurant's orders"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StandardResponse<RestaurantOrderSummaryResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateOrderStatusRequest request
    ) {
        var response = StandardResponse.success(
                orderService.updateStatus(id, request.status()),
                "Status do pedido atualizado!"
        );
        return ResponseEntity.ok(response);
    }

}
