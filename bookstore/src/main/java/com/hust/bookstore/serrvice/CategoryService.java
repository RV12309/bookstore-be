package com.hust.bookstore.serrvice;

import com.hust.bookstore.dto.request.CategoryRequest;
import com.hust.bookstore.dto.request.SearchCategoryRequest;
import com.hust.bookstore.dto.request.UpdateCategoryRequest;
import com.hust.bookstore.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategory(UpdateCategoryRequest request);

    void delete(Long id);

    CategoryResponse getDetail(Long id);

    List<CategoryResponse> getAll();

    Page<CategoryResponse> searchCategories(SearchCategoryRequest request);

}
