package com.hust.bookstore.controller;

import com.hust.bookstore.dto.request.*;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.CategoryResponse;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.serrvice.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Thêm mới danh mục")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest bookRequest) {
        return ResponseEntity.ok(categoryService.createCategory(bookRequest));
    }

    @Operation(summary = "Cập nhật danh mục")
    @PutMapping
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody UpdateCategoryRequest bookRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(bookRequest));
    }

    @Operation(summary = "Xóa danh mục theo mã")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Lấy thông tin chi tiết danh mục theo mã")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getDetail(id));
    }

    @Operation(summary = "Tìm kiếm danh mục")
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> searchCategories(@RequestBody SearchCategoryRequest request) {
        return ResponseEntity.ok(categoryService.searchCategories(request));
    }

    @Operation(summary = "Lấy tất cả danh sách danh mục")
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

}
