package com.spring.task.controller;

import com.spring.task.entity.Product;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.ProductRequest;
import com.spring.task.payload.response.ProductResponse;
import com.spring.task.service.ProductService;
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
 * Controller class for handling product-related endpoints.
 * Provides APIs for managing products such as creating, updating, deleting, and retrieving products.
 *
 * @author Rahil Sathwara
 * @since 2024-03-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product.
     * Only users with ROLE_ADMIN authority are allowed to access this endpoint.
     * Validates the incoming product request and creates a new product entity.
     * Returns a response entity with the created product and a success message.
     *
     * @param productRequest The request body containing product details.
     * @return A response entity with the created product and a success message.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product savedProduct = productService.createProduct(productRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.CREATED, "Product created successfully", productService.mapEntityToResponse(savedProduct)), HttpStatus.CREATED);
    }

    /**
     * Retrieves a product by its ID.
     * Users with ROLE_ADMIN, ROLE_MANAGER, or ROLE_USER authority can access this endpoint.
     * Retrieves the product from the database by its ID and returns it in the response.
     *
     * @param id The ID of the product to retrieve.
     * @return A response entity with the retrieved product and a success message.
     * @throws ResourceNotFoundException if the product with the given ID is not found.
     */
    @GetMapping("{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER')")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable("productId") Long id) {
        Product productById = productService.getProductById(id).orElseThrow(() -> new ResourceNotFoundException("Error: Product is not found."));

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get Product By ID ", productService.mapEntityToResponse(productById)), HttpStatus.OK);
    }

    /**
     * Retrieves all products.
     * Users with ROLE_ADMIN, ROLE_MANAGER, or ROLE_USER authority can access this endpoint.
     * Retrieves all products from the database and returns them in the response.
     *
     * @return A response entity with the list of all products and a success message.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponse> getProducts = productService.getAllProductsLst();

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Get all products", getProducts), HttpStatus.OK);
    }

    /**
     * Edits an existing product.
     * Only users with ROLE_ADMIN authority can access this endpoint.
     * Retrieves the product with the specified ID from the database, updates it with the new data provided
     * in the request body, and returns the updated product in the response.
     *
     * @param id             The ID of the product to be edited.
     * @param productRequest The updated product data.
     * @return A response entity with the updated product and a success message.
     * @throws ResourceNotFoundException    If the product with the specified ID is not found.
     */
    @PutMapping("{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> editProduct(@PathVariable("productId") Long id, @Valid @RequestBody ProductRequest productRequest) {
        Product savedProduct = productService.updateProduct(id, productRequest);

        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Product updated successfully", productService.mapEntityToResponse(savedProduct)), HttpStatus.OK);
    }

    /**
     * Deletes a product with the specified ID.
     * Only users with ROLE_ADMIN authority can access this endpoint.
     * Deletes the product with the specified ID from the database.
     *
     * @param id The ID of the product to be deleted.
     * @return A response entity with a success message.
     * @throws ResourceNotFoundException If the product with the specified ID is not found.
     */
    @DeleteMapping("{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long id) {
        productService.deleteById(id);
        return new ResponseEntity<>(new ApiResponse(LocalDateTime.now(), HttpStatus.OK, "Delete product: " + id, null), HttpStatus.OK);
    }
}