package com.spring.task.service.serviceimpl;


import com.spring.task.entity.Category;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.CategoryRequest;
import com.spring.task.payload.response.CategoryResponse;
import com.spring.task.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void testCreateCategory_Success() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Test Category");
        categoryRequest.setDescription("Test Description");
        categoryRequest.setImageUrl("http://test.com/image.jpg");

        when(categoryRepository.findByNameIgnoreCase(categoryRequest.getName())).thenReturn(Optional.empty());

        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryRequest, newCategory);
        newCategory.setUpdatedAt(LocalDateTime.now());
        newCategory.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        Category createdCategory = categoryService.createCategory(categoryRequest);

        assertNotNull(createdCategory);
        assertEquals(categoryRequest.getName(), createdCategory.getName());
        assertEquals(categoryRequest.getDescription(), createdCategory.getDescription());
        assertEquals(categoryRequest.getImageUrl(), createdCategory.getImageUrl());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void testCreateCategory_AlreadyExists() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Existing Category");

        when(categoryRepository.findByNameIgnoreCase(categoryRequest.getName())).thenReturn(Optional.of(new Category()));

        assertThrows(ResourceAlreadyExistException.class, () -> categoryService.createCategory(categoryRequest));
    }

    @Test
    public void testGetCategoryById_Success() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(categoryId);

        assertTrue(result.isPresent());
        assertEquals(category, result.get());
    }

    @Test
    public void testGetAllCategoriesLst() {
        List<Category> categories = Arrays.asList(
                new Category(),
                new Category()
        );

        when(categoryRepository.findAll()).thenReturn(categories);
        List<CategoryResponse> categoryResponses = categoryService.getAllCategoriesLst();

        assertEquals(categories.size(), categoryResponses.size());
        verify(categoryRepository).findAll();
    }

    @Test
    public void testDeleteById_Success() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        assertDoesNotThrow(() -> categoryService.deleteById(categoryId));

        verify(categoryRepository).delete(category);
    }

    @Test
    public void testDeleteById_NotFound() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteById(categoryId));
    }

    @Test
    public void testUpdateCategory_Success() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Updated Category");
        categoryRequest.setDescription("Updated Description");
        categoryRequest.setImageUrl("http://test.com/updated.jpg");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Existing Category");
        existingCategory.setDescription("Existing Description");
        existingCategory.setImageUrl("http://test.com/existing.jpg");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryRequest.getName(), categoryId)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        Category updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);

        assertNotNull(updatedCategory);
        assertEquals(categoryRequest.getName(), updatedCategory.getName());
        assertEquals(categoryRequest.getDescription(), updatedCategory.getDescription());
        assertEquals(categoryRequest.getImageUrl(), updatedCategory.getImageUrl());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void testUpdateCategory_NotFound() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
    }

    @Test
    public void testUpdateCategory_AlreadyExists() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Existing Category");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryRequest.getName(), categoryId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> categoryService.updateCategory(categoryId, categoryRequest));
    }

    @Test
    public void testMapEntityToResponse() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Test Description");
        category.setImageUrl("http://test.com/image.jpg");

        CategoryResponse response = categoryService.mapEntityToResponse(category);

        assertNotNull(response);
        assertEquals(category.getId(), response.getId());
        assertEquals(category.getName(), response.getName());
        assertEquals(category.getDescription(), response.getDescription());
        assertEquals(category.getImageUrl(), response.getImageUrl());
    }
}