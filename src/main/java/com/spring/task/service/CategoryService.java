package com.spring.task.service;

import com.spring.task.entity.Category;
import com.spring.task.payload.request.CategoryRequest;
import com.spring.task.payload.response.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(CategoryRequest categoryRequest);

    CategoryResponse mapEntityToResponse(Category savedCategory);

    Optional<Category> getCategoryById(Long id);

    List<CategoryResponse> getAllCategoriesLst();

    void deleteById(Long id);

    Category updateCategory(Long id, CategoryRequest categoryRequest);
}
