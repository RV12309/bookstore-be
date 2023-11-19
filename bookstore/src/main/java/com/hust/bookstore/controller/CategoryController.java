package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.CategoryRequest;
import com.hust.bookstore.dto.request.SearchCategoryRequest;
import com.hust.bookstore.dto.request.UpdateCategoryRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.CategoryResponse;
import com.hust.bookstore.enumration.ResponseCode;
import com.hust.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest bookRequest) {
        return ResponseEntity.ok(BaseResponse.<CategoryResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(categoryService.createCategory(bookRequest)).build());
    }

    @Operation(summary = "Cập nhật danh mục")
    @PutMapping
    public ResponseEntity<BaseResponse<CategoryResponse>> updateCategory(
            @Valid @RequestBody UpdateCategoryRequest bookRequest) {
        return ResponseEntity.ok(BaseResponse.<CategoryResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(categoryService.updateCategory(bookRequest)).build());
    }

    @Operation(summary = "Xóa danh mục theo mã")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Object>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Lấy thông tin chi tiết danh mục theo mã")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.<CategoryResponse>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(categoryService.getDetail(id)).build());
    }

    @Operation(summary = "Tìm kiếm danh mục")
    @GetMapping
    public ResponseEntity<BaseResponse<PageDto<CategoryResponse>>> searchCategories(
            @RequestBody SearchCategoryRequest request) {
        return ResponseEntity.ok(BaseResponse.<PageDto<CategoryResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message())
                .data(categoryService.searchCategories(request)).build());
    }

    @Operation(summary = "Lấy tất cả danh sách danh mục")
    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(BaseResponse.<List<CategoryResponse>>builder().code(ResponseCode.SUCCESS.code())
                .message(ResponseCode.SUCCESS.message()).data(categoryService.getAll()).build());
    }

}
