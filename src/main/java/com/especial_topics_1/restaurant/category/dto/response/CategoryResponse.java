package com.especial_topics_1.restaurant.category.dto.response;

import com.especial_topics_1.restaurant.category.Category;

public record CategoryResponse(Integer id, String name) {
    public CategoryResponse(Category category) {
        this(category.getId(), category.getName());
    }
}
