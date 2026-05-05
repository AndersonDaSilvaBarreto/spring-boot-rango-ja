package com.especial_topics_1.restaurant.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o JOIN FETCH o.restaurant WHERE o.customer.id = :customerId")
    Page<Order> findAllByCustomerId(UUID customerId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.restaurant.id = :restaurantId")
    Page<Order> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithCustomer(@Param("id") UUID id);

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.restaurant " +
            "JOIN FETCH o.customer " +
            "JOIN FETCH o.orderItems items " +
            "JOIN FETCH items.dish " +
            "LEFT JOIN FETCH o.coupon " + // Fetch no prato para pegar o nome e imagem no DTO
            "WHERE o.id = :id")
    Optional<Order> findFullOrderById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"restaurant", "customer", "coupon"})
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId")
    Page<Order> findFullOrdersByCustomerId( @Param("customerId") UUID customerId,Pageable pageable);

    @EntityGraph(attributePaths = {"restaurant", "customer", "coupon"})
    @Query("SELECT o FROM Order o WHERE o.restaurant.id = :restaurantId")
    Page<Order> findFullOrdersByRestaurantId(@Param("restaurantId") UUID restaurantId, Pageable pageable);
}
