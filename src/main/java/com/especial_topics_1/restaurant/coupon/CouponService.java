package com.especial_topics_1.restaurant.coupon;

import com.especial_topics_1.restaurant.auth.AuthenticatedUserService;
import com.especial_topics_1.restaurant.coupon.dto.request.CreateGlobalCouponRequest;
import com.especial_topics_1.restaurant.coupon.dto.request.CreateRestaurantCouponRequest;
import com.especial_topics_1.restaurant.coupon.dto.response.CouponResponse;
import com.especial_topics_1.restaurant.coupon.dto.response.FindCouponsByNameAndRestaurantId;
import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.exception.ResourceNotFoundException;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final RestaurantRepository restaurantRepository;
    private final CouponRepository couponRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public CouponResponse createRestaurantCoupon(CreateRestaurantCouponRequest req) {
        Restaurant restaurant = restaurantRepository.findById(req.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante não encontrado!"));
        User loggedUser = authenticatedUserService.getCurrentUser();

        if (!loggedUser.equals(restaurant.getOwner())) {
            throw new BusinessException("Você não tem permissão para criar um cupom para este restaurante!");
        }

        boolean couponExists = couponRepository.existsByNameAndRestaurantId(req.name(), restaurant.getId());

        if (couponExists) {
            throw new BusinessException("Este restaurante já possúi um cupom com o nome " + req.name() + "!");
        }

        discountValidation(req.discountType(), req.discountValue(), req.minValue());

        Coupon coupon = Coupon.builder()
                .name(req.name())
                .restaurant(restaurant)
                .endDate(req.endDate())
                .minValue(req.minValue())
                .discountValue(req.discountValue())
                .discountType(req.discountType())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return new CouponResponse(savedCoupon);


    }

    private void discountValidation(DiscountType discountType, BigDecimal discountValue, BigDecimal minValue) {
        if (discountType.equals(DiscountType.PERCENTAGE)) {
            BigDecimal limit = new BigDecimal("100");
            if (discountValue.compareTo(limit) > 0) {
                throw new BusinessException("O desconto não pode ser maior que 100%");
            }
        }

        if (discountType.equals(DiscountType.SUBTRACT)) {
            if (discountValue.compareTo(minValue) > 0) {
                throw new BusinessException("O desconto não pode ser maior que o valor mínimo");
            }
        }
    }

    @Transactional
    public CouponResponse createGlobalCoupon(CreateGlobalCouponRequest req) {
        User loggedUser = authenticatedUserService.getCurrentUser();
        if (!loggedUser.hasRole(Role.ADMIN)) {
            throw new BusinessException("Você não tem permissão para criar um cupom global");
        }

        discountValidation(req.discountType(), req.discountValue(), req.minValue());

        boolean globalCouponExits = couponRepository.existsGlobalCoupon(req.name());

        if (globalCouponExits) {
            throw new BusinessException("Já existe um cupom com este nome");
        }
        Coupon coupon = Coupon.builder()
                .name(req.name())
                .restaurant(null)
                .endDate(req.endDate())
                .minValue(req.minValue())
                .discountValue(req.discountValue())
                .discountType(req.discountType())
                .build();

        Coupon savedCoupon = couponRepository.save(coupon);
        return new CouponResponse(savedCoupon);

    }

    @Transactional(readOnly = true)
    public FindCouponsByNameAndRestaurantId findValidCouponsByNameAndRestaurantId(String name, UUID restaurantId) {
        List<Coupon> coupons = couponRepository.findCouponsByNameAndScope(name.toUpperCase(), restaurantId);

        Coupon restaurantCoupon = coupons.stream()
                .filter(c -> c.getRestaurant() != null)
                .findFirst().orElse(null);

        Coupon globalCoupon = coupons.stream()
                .filter(c -> c.getRestaurant() == null)
                .findFirst().orElse(null);

        return new FindCouponsByNameAndRestaurantId(
                restaurantCoupon != null ? new CouponResponse(restaurantCoupon) : null,
                globalCoupon != null ? new CouponResponse(globalCoupon) : null
        );
    }

    @Transactional(readOnly = true)
    public Page<CouponResponse> findValidCouponsByRestaurantIdWithGlobalCoupons(
            UUID restaurantId,
            Pageable pageable
    ) {
        Page<Coupon> coupons = couponRepository.findValidCouponsByRestaurantIdOrRestaurantNull(
                restaurantId,
                pageable);

        return coupons.map(CouponResponse::new);
    }

    @Transactional
    public CouponResponse invalidateCoupon(UUID couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cumpo não encontrado"));
        User loggedUser = authenticatedUserService.getCurrentUser();
        boolean isAdmin = loggedUser.hasRole(Role.ADMIN);

        if(coupon.getRestaurant() == null) {
            if (!isAdmin) {
                throw new BusinessException("Você não tem permissão para invalidar este cupom global!");
            }
        }else {
            boolean isOwner = loggedUser.equals(coupon.getRestaurant().getOwner());
            if (!isOwner && !isAdmin) {
                throw new BusinessException("Você não tem permissão para invalidar o cupom deste restaurante!");
            }
        }

        coupon.setEndDate(Instant.now());
        Coupon savedCoupon = couponRepository.save(coupon);

        return new CouponResponse(savedCoupon);

    }
}
