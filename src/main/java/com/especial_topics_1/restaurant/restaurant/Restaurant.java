package com.especial_topics_1.restaurant.restaurant;

import com.especial_topics_1.restaurant.dish.Dish;
import com.especial_topics_1.restaurant.user.User;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Dish> dishes = new ArrayList<>();

    public void addDish(Dish dish) {
        this.dishes.add(dish);
        dish.setRestaurant(this);
    }
    public void removeDish(Dish dish) {
        this.dishes.remove(dish);
        dish.setRestaurant(null);
    }

    @Column( name = "name",nullable = false)
    private String name;

    @Column( name = "description",columnDefinition = "TEXT")
    private String description;


    @NotNull
    @Column(name = "is_open", nullable = false)
    @Builder.Default
    private Boolean isOpen = false;

    @Column(name = "logo_url", length = 1024)
    private String logoUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.createdAt == null)  {
            this.createdAt = Instant.now();
        }
    }

   @Override
    public boolean equals(Object o) {
        if(this == o) return true;
      if(!(o instanceof Restaurant that)) return false;
      return getId() != null && getId().equals(that.getId());

   }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
