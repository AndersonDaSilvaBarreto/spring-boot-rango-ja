package com.especial_topics_1.restaurant.order;

import com.especial_topics_1.restaurant.auth.AuthenticatedUserService;
import com.especial_topics_1.restaurant.coupon.Coupon;
import com.especial_topics_1.restaurant.coupon.CouponRepository;
import com.especial_topics_1.restaurant.dish.Dish;
import com.especial_topics_1.restaurant.dish.DishRepository;
import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
import com.especial_topics_1.restaurant.order.dto.request.RegisterOrderItemRequest;
import com.especial_topics_1.restaurant.order.dto.request.RegisterOrderRequest;
import com.especial_topics_1.restaurant.order.dto.response.OrderDetailResponse;
import com.especial_topics_1.restaurant.order.dto.response.RegisterOrderResponse;
import com.especial_topics_1.restaurant.order.dto.response.RestaurantOrderSummaryResponse;
import com.especial_topics_1.restaurant.restaurant.Restaurant;
import com.especial_topics_1.restaurant.restaurant.RestaurantRepository;
import com.especial_topics_1.restaurant.user.Role;
import com.especial_topics_1.restaurant.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final CouponRepository couponRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public RegisterOrderResponse create(RegisterOrderRequest req) {
        User loggedUser = authenticatedUserService.getCurrentUser();

        Restaurant restaurant = restaurantRepository.findById(req.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!"));
        if (loggedUser.equals(restaurant.getOwner())) {
            throw new BusinessException("Não é permitido realizar um pedido para si mesmo!");
        }

        if(restaurant.getIsOpen().equals(false)) {
            throw new BusinessException(
                    "O restaurante está fechado, não é possível solicitar pedido agora ");
        }

        Order order = Order.builder()
                .restaurant(restaurant)
                .customer(loggedUser)
                .totalAmount(BigDecimal.ZERO)
                .build();


        for (RegisterOrderItemRequest orderItemRequest : req.orderItems()) {
            Dish dish = dishRepository.findById(orderItemRequest.dishId()).orElseThrow(() ->
                    new ResourceNotFoundException("Prato com o id "
                            + orderItemRequest.dishId()
                            + " não encontrado"));
            if (!dish.getRestaurant().equals(restaurant)) {
                throw new BusinessException("O prato '" +
                        dish.getName() +
                        "' não pertence ao restaurante selecionado.");
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .dish(dish)
                    .quantity(orderItemRequest.quantity())
                    .unitPrice(dish.getPrice())
                    .build();
            order.addOrderItem(orderItem);

            BigDecimal itemTotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            order.setTotalAmount(order.getTotalAmount().add(itemTotal));


        }

        if(req.couponId() != null) {
            Coupon coupon = couponRepository.findById(req.couponId())
                    .orElseThrow(() -> new BusinessException("Cupom inválido!"));
            if(coupon.getEndDate().isBefore(Instant.now())) {
                throw new BusinessException("Cupom inválido!");
            }

            if (coupon.getRestaurant() != null &&
                    !coupon.getRestaurant().equals(restaurant)) {
                throw new BusinessException("Este cupom não é válido para este restaurante.");
            }

            if (order.getTotalAmount().compareTo(coupon.getMinValue()) < 0) {
                throw new BusinessException(
                        "O pedido não possui o valor mínimo de R$"
                                + coupon.getMinValue() + " reais para o cupom enviado");
            }

            BigDecimal discountToApply = switch (coupon.getDiscountType()) {
                case SUBTRACT -> coupon.getDiscountValue();

                case PERCENTAGE -> order.getTotalAmount()
                        .multiply(coupon.getDiscountValue())
                        .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
            };

            BigDecimal newTotal = order.getTotalAmount().subtract(discountToApply);
            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("O valor do pedido não pode ser negativo!");
            }

            order.setTotalAmount(newTotal);
            order.setDiscountAmount(discountToApply);
            order.setCoupon(coupon);
        }



        order.setTotalAmount(order.getTotalAmount().setScale(2, RoundingMode.HALF_UP));
        orderRepository.save(order);
        return new RegisterOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDetailResponse> getMyOrders(Pageable pageable) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        Page<Order> orders = orderRepository.findFullOrdersByCustomerId(loggedUser.getId(), pageable);
        return orders.map(OrderDetailResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<OrderDetailResponse> getRestaurantOrders(UUID restaurantId, Pageable pageable) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!") );
        if(!loggedUser.hasRole(Role.ADMIN) && !loggedUser.equals(restaurant.getOwner())) {
            throw new BusinessException("Você não tem permissão de acesso!");
        }
        Page<Order> orders = orderRepository.findFullOrdersByRestaurantId(restaurantId,pageable);
        return orders.map(OrderDetailResponse::new);


    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetailById(UUID id) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        Order order = orderRepository.findFullOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        if(!loggedUser.hasRole(Role.ADMIN) &&
                !loggedUser.equals(order.getCustomer())&&
                !loggedUser.equals(order.getRestaurant().getOwner())) {
            throw new BusinessException("Você não tem permissão de acesso");
        }
        return new OrderDetailResponse(order);
    }

    @Transactional
    public RestaurantOrderSummaryResponse updateStatus(UUID id, OrderStatus newStatus) {
        Order order = orderRepository.findByIdWithCustomer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado!"));
        User loggedUser = authenticatedUserService.getCurrentUser();
        if (!loggedUser.hasRole(Role.ADMIN) && !order.getRestaurant().getOwner().equals(loggedUser)) {
            throw new BusinessException("Você não tem permissão para alterar este pedido.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Este pedido já foi entregue e não pode mais ser alterado.");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Este pedido foi cancelado e não pode mais ser alterado.");
        }

        // Se o novo status for igual ao atual, não fazemos nada (ou avisamos o erro)
        if (order.getStatus() == newStatus) {
            throw new BusinessException("O pedido já está com o status " + newStatus);
        }

        order.setStatus(newStatus);
        return new RestaurantOrderSummaryResponse(order);
    }
}
