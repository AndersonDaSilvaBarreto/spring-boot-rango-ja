package com.especial_topics_1.restaurant.category;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tb_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name",unique = true, nullable = false, length = 100)
    String name;

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
       if(!(o instanceof Category that)) return false;
       return getId() != null && getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
