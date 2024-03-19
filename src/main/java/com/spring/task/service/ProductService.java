package com.spring.task.service;

import com.spring.task.entity.Product;
import com.spring.task.payload.request.ProductRequest;
import com.spring.task.payload.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(ProductRequest productRequest);

    ProductResponse mapEntityToResponse(Product savedProduct);

    Optional<Product> getProductById(Long id);

    List<ProductResponse> getAllProductsLst();

    void deleteById(Long id);

    Product updateProduct(Long id, ProductRequest productRequest);
}