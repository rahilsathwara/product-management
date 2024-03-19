package com.spring.task.controller;

import com.spring.task.entity.Category;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.CategoryRequest;
import com.spring.task.payload.response.CategoryResponse;
import com.spring.task.service.CategoryService;
import com.spring.task.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for handling category-related endpoints.
 * Provides APIs for managing categories such as creating, updating, deleting, and retrieving categories.
 *
 * @author Rahil Sathwara
 * @since 2024-03-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category/")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new category with the provided details.
     * Only users with the 'ROLE_ADMIN' authority are allowed to create categories.
     *
     * @param categoryRequest The request body containing the details of the category to be created.
     * @return A ResponseEntity containing the ApiResponse with the details of the newly created category.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category savedCategory = categoryService.createCategory(categoryRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.CREATED, "Category created successfully", categoryService.mapEntityToResponse(savedCategory)), HttpStatus.CREATED);
    }

    /**
     * Retrieves a category by its ID.
     * Only users with the 'ROLE_ADMIN' or 'ROLE_MANAGER' authority are allowed to access this endpoint.
     *
     * @param id The ID of the category to retrieve.
     * @return A ResponseEntity containing the ApiResponse with the details of the retrieved category.
     * @throws ResourceNotFoundException if the category with the specified ID is not found.
     */
    @GetMapping("{categoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable("categoryId") Long id) {
        Category category = categoryService.getCategoryById(id).orElseThrow(() -> new ResourceNotFoundException("Error: Category is not found."));

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get Category By ID ", categoryService.mapEntityToResponse(category)), HttpStatus.OK);
    }

    /**
     * Retrieves all categories.
     * Only users with the 'ROLE_ADMIN' or 'ROLE_MANAGER' authority are allowed to access this endpoint.
     *
     * @return A ResponseEntity containing the ApiResponse with a list of all categories.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse> getAllCategories() {
        List<CategoryResponse> allCategoriesLst = categoryService.getAllCategoriesLst();

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get all Categories", allCategoriesLst), HttpStatus.OK);
    }

    /**
     * Updates a category with the specified ID.
     * Only users with the 'ROLE_ADMIN' authority are allowed to access this endpoint.
     *
     * @param id               The ID of the category to update.
     * @param categoryRequest  The request body containing the updated category information.
     * @return A ResponseEntity containing the ApiResponse with the updated category information.
     */
    @PutMapping("{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> editCategory(@PathVariable("categoryId") Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category savedCategory = categoryService.updateCategory(id, categoryRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Category updated successfully", categoryService.mapEntityToResponse(savedCategory)), HttpStatus.OK);
    }

    /**
     * Deletes a category with the specified ID.
     * Only users with the 'ROLE_ADMIN' authority are allowed to access this endpoint.
     *
     * @param id The ID of the category to delete.
     * @return A ResponseEntity containing the ApiResponse indicating the deletion success.
     */
    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable("categoryId") Long id) {
        categoryService.deleteById(id);
        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Delete Category: " + id, null), HttpStatus.OK);
    }
}