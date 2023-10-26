package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.CategoryResponse;
import com.hust.bookstore.serrvice.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest bookRequest) {
        return ResponseEntity.ok(categoryService.createCategory(bookRequest));
    }

    @PutMapping
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody UpdateCategoryRequest bookRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(bookRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getDetail(id));
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> searchCategories(@RequestBody SearchCategoryRequest request) {
        return ResponseEntity.ok(categoryService.searchCategories(request));
    }

}
