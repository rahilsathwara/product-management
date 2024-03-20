package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Category;
import com.spring.task.entity.Product;
import com.spring.task.entity.User;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.ProductRequest;
import com.spring.task.payload.response.ProductResponse;
import com.spring.task.repository.ProductRepository;
import com.spring.task.service.CategoryService;
import com.spring.task.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;

    @InjectMocks
    private ProductServiceImpl productService;
    @Spy
    private Logger logger;

    @Test
    public void testCreateProduct_Success() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategoryId(1L);
        productRequest.setUserId("2");

        when(productRepository.findByNameIgnoreCase(productRequest.getName())).thenReturn(Optional.empty());

        Category category = new Category();
        category.setId(1L);
        when(categoryService.getCategoryById(productRequest.getCategoryId())).thenReturn(Optional.of(category));

        User user = new User();
        user.setId(2L);
        when(userService.getUserById(productRequest.getUserId())).thenReturn(Optional.of(user));

        Product validatedProduct = new Product();
        BeanUtils.copyProperties(productRequest, validatedProduct);
        validatedProduct.setCategory(category);
        validatedProduct.setUser(user);
        validatedProduct.setSku(UUID.randomUUID().toString());
        validatedProduct.setCreatedAt(LocalDateTime.now());
        validatedProduct.setUpdatedAt(LocalDateTime.now());

        // Ensure the save method is stubbed with the same arguments as the validatedProduct
        when(productRepository.save(any(Product.class))).thenReturn(validatedProduct);

        // Act
        Product result = productService.createProduct(productRequest);

        // Assert
        assertEquals(validatedProduct, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testValidateProductRequest() throws Exception {
        Long categoryId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setCategoryId(categoryId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        User user = new User();
        user.setId(2L);
        when(userService.getUserById(productRequest.getUserId())).thenReturn(Optional.of(user));


        when(productRepository.findByNameIgnoreCase(productRequest.getName())).thenReturn(Optional.empty());
        when(categoryService.getCategoryById(productRequest.getCategoryId())).thenReturn(Optional.of(category));

        Method method = ProductServiceImpl.class.getDeclaredMethod("validateProductRequest", ProductRequest.class);
        method.setAccessible(true);

        Product validatedProduct = (Product) method.invoke(productService, productRequest);

        assertEquals(productRequest.getName(), validatedProduct.getName());
        assertEquals(category, validatedProduct.getCategory());
        assertEquals(user, validatedProduct.getUser());
    }

    @Test
    public void testCreateProduct_AlreadyExists() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Existing Product");
        productRequest.setCategoryId(1L);

        when(productRepository.findByNameIgnoreCase(productRequest.getName())).thenReturn(Optional.of(new Product()));

        assertThrows(ResourceAlreadyExistException.class, () -> productService.createProduct(productRequest));
    }

    @Test
    public void testGetProductById_Success() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    public void testGetAllProductsLst() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponse> productResponses = productService.getAllProductsLst();

        assertEquals(products.size(), productResponses.size());
        verify(productRepository).findAll();
    }

    @Test
    public void testDeleteById_Success() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.deleteById(productId));

        verify(productRepository).delete(product);
    }

    @Test
    public void testDeleteById_NotFound() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteById(productId));
    }

    @Test
    public void testUpdateProduct_Success() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product");
        productRequest.setCategoryId(1L);
        productRequest.setUserId("2");

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product Name");
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        when(productRepository.existsByNameIgnoreCaseAndIdNot(productRequest.getName(), productId)).thenReturn(false);

        Category category = new Category();
        category.setId(1L);
        when(categoryService.getCategoryById(productRequest.getCategoryId())).thenReturn(Optional.of(category));

        User user = new User();
        user.setId(2L);
        when(userService.getUserById(productRequest.getUserId())).thenReturn(Optional.of(user));

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName(productRequest.getName());
        updatedProduct.setCategory(category);
        updatedProduct.setUser(user);
        updatedProduct.setUpdatedAt(LocalDateTime.now());

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId, productRequest);

        assertEquals(updatedProduct, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_NotFound() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, productRequest));
    }

    @Test
    public void testUpdateProduct_AlreadyExists() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Existing Product");
        productRequest.setCategoryId(1L);

        Product existingProduct = new Product();
        existingProduct.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByNameIgnoreCaseAndIdNot(productRequest.getName(), productId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class, () -> productService.updateProduct(productId, productRequest));
    }

    @Test
    public void testMapEntityToResponse() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setCategory(category);

        ProductResponse response = productService.mapEntityToResponse(product);

        assertNotNull(response);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getName(), response.getName());
        assertEquals(category.getId(), response.getCategory().getId());
        assertEquals(category.getName(), response.getCategory().getName());
    }
}