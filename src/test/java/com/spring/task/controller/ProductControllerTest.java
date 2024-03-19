package com.spring.task.controller;

import com.spring.task.entity.Product;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.ProductRequest;
import com.spring.task.payload.response.ProductResponse;
import com.spring.task.service.ProductService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddProduct() {
        ProductRequest productRequest = new ProductRequest();
        Product savedProduct = new Product();

        when(productService.createProduct(productRequest)).thenReturn(savedProduct);
        when(productService.mapEntityToResponse(savedProduct)).thenReturn(new ProductResponse());

        ResponseEntity<ApiResponse> response = productController.addProduct(productRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Product created successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetProductById_ProductFound() {
        Long productId = 1L;
        Product product = new Product();

        when(productService.getProductById(productId)).thenReturn(Optional.of(product));
        when(productService.mapEntityToResponse(product)).thenReturn(new ProductResponse());

        ResponseEntity<ApiResponse> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get Product By ID ", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetProductById_ProductNotFound() {
        Long productId = 1L;
        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productController.getProductById(productId));
        assertEquals("Error: Product is not found.", exception.getMessage());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllProducts() {
        List<ProductResponse> productResponseList = new ArrayList<>();

        when(productService.getAllProductsLst()).thenReturn(productResponseList);

        ResponseEntity<ApiResponse> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Get all products", response.getBody().getMessage());
        assertEquals(productResponseList, response.getBody().getData());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditProduct() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();

        Product updatedProduct = new Product();

        when(productService.updateProduct(productId, productRequest)).thenReturn(updatedProduct);
        when(productService.mapEntityToResponse(updatedProduct)).thenReturn(new ProductResponse());

        ResponseEntity<ApiResponse> response = productController.editProduct(productId, productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product updated successfully", response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteProduct() {
        Long productId = 1L;

        doNothing().when(productService).deleteById(productId);

        ResponseEntity<ApiResponse> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete product: " + productId, response.getBody().getMessage());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), response.getBody().getTimestamp().truncatedTo(ChronoUnit.SECONDS));
    }
}
