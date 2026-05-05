package com.especial_topics_1.restaurant.category;

import com.especial_topics_1.restaurant.category.dto.response.CategoryResponse;
import com.especial_topics_1.restaurant.standard.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<CategoryResponse>> findById(@PathVariable int id) {
        StandardResponse<CategoryResponse> response = StandardResponse.success(
                categoryService.findById(id),
                "Category found"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping()
    public ResponseEntity<StandardResponse<List<CategoryResponse>>> findAll() {
        StandardResponse<List<CategoryResponse>> response = StandardResponse.success(
                categoryService.findAll(),
                "Category list"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
