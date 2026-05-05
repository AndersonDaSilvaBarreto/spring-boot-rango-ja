package com.especial_topics_1.restaurant.order;

import com.especial_topics_1.restaurant.dish.Dish;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id",nullable = false)
    private Dish dish;
    
    @Column(name = "quantity",nullable = false)
    private Integer quantity;

    @Column(name = "unit_price",nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof OrderItem that)) return false;
        return getId() != null && getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
