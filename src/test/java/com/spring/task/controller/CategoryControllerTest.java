package com.spring.task.controller;

import com.spring.task.entity.Category;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.CategoryRequest;
import com.spring.task.payload.response.CategoryResponse;
import com.spring.task.service.CategoryService;
import com.spring.task.web.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        Category savedCategory = new Category();
        CategoryResponse categoryResponse = new CategoryResponse();

        when(categoryService.createCategory(categoryRequest)).thenReturn(savedCategory);
        when(categoryService.mapEntityToResponse(savedCategory)).thenReturn(categoryResponse);

        ResponseEntity<ApiResponse> response = categoryController.createCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Category created successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetCategoryById_CategoryFound() {
        Long categoryId = 1L;
        Category category = new Category();

        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.of(category));
        when(categoryService.mapEntityToResponse(category)).thenReturn(new CategoryResponse());

        ResponseEntity<ApiResponse> response = categoryController.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get Category By ID ", response.getBody().getMessage()); // Note the trailing space
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetCategoryById_CategoryNotFound() {
        Long categoryId = 1L;
        when(categoryService.getCategoryById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryController.getCategoryById(categoryId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllCategories() {
        List<CategoryResponse> allCategoriesLst = new ArrayList<>();

        when(categoryService.getAllCategoriesLst()).thenReturn(allCategoriesLst);

        ResponseEntity<ApiResponse> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get all Categories", response.getBody().getMessage());
        assertEquals(allCategoriesLst, response.getBody().getData());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditCategory() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();

        Category updatedCategory = new Category();

        when(categoryService.updateCategory(categoryId, categoryRequest)).thenReturn(updatedCategory);
        when(categoryService.mapEntityToResponse(updatedCategory)).thenReturn(new CategoryResponse());

        ResponseEntity<ApiResponse> response = categoryController.editCategory(categoryId, categoryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category updated successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryService).deleteById(categoryId);

        ResponseEntity<ApiResponse> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete Category: " + categoryId, response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }
}
