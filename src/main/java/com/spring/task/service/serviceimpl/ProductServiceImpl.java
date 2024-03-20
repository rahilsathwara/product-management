package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Category;
import com.spring.task.entity.Product;
import com.spring.task.entity.User;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.ProductRequest;
import com.spring.task.payload.response.CategoryResponse;
import com.spring.task.payload.response.ProductResponse;
import com.spring.task.repository.ProductRepository;
import com.spring.task.service.CategoryService;
import com.spring.task.service.ProductService;
import com.spring.task.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest) {
        logger.info("Creating product with name: {}", productRequest.getName());

        Product product = validateProductRequest(productRequest);
        product.setSku(UUID.randomUUID().toString());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    private Product validateProductRequest(ProductRequest productRequest) {
        logger.info("Validating product request for product: {}", productRequest.getName());

        // validate product exist or not
        productRepository.findByNameIgnoreCase(productRequest.getName())
                .ifPresent(role -> {
                    logger.error("Product {} already exists", productRequest.getName());
                    throw new ResourceAlreadyExistException(productRequest.getName() + " is already exist");
                });

        // validate category exist or not
        Category category = categoryService.getCategoryById(productRequest.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", productRequest.getCategoryId());
                    return new ResourceNotFoundException("Category Not found with ID: " + productRequest.getCategoryId());
                });

        // validate user
        User user = userService.getUserById(productRequest.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", productRequest.getUserId());
                    return new ResourceNotFoundException("User not found with ID: " + productRequest.getUserId());
                });

        Product newProduct = new Product();
        BeanUtils.copyProperties(productRequest, newProduct);
        newProduct.setCategory(category);
        newProduct.setUser(user);
        logger.info("Product request validated successfully for product: {}", productRequest.getName());
        return newProduct;
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<ProductResponse> getAllProductsLst() {
        return productRepository.findAll().stream()
                .map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting product with ID: {}", id);
        Product product = getProductById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Error: Product is not found.");
                });

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest) {
        logger.info("Updating product with ID: {}", id);
        Product product = getProductById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Error: Product is not found.");
                });

        // check product name exist or not
        if (productRepository.existsByNameIgnoreCaseAndIdNot(productRequest.getName(), id)) {
            logger.error("Product with name {} already exists", productRequest.getName());
            throw new ResourceAlreadyExistException("Product with name " + productRequest.getName() + " already exists");
        }

        // validate category exist or not
        Category category = categoryService.getCategoryById(productRequest.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: {}", productRequest.getCategoryId());
                    return new ResourceNotFoundException("Category Not found with ID: " + productRequest.getCategoryId());
                });

        // validate user
        User user = userService.getUserById(productRequest.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", productRequest.getUserId());
                    return new ResourceNotFoundException("User not found with ID: " + productRequest.getUserId());
                });

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setWeight(productRequest.getWeight());
        product.setWeightUnit(productRequest.getWeightUnit());
        product.setBrand(productRequest.getBrand());
        product.setCategory(category);
        product.setUser(user);
        product.setInventory(productRequest.getInventory());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public ProductResponse mapEntityToResponse(Product savedProduct) {
        CategoryResponse categoryResponse = new CategoryResponse();
        BeanUtils.copyProperties(savedProduct.getCategory(), categoryResponse);

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(savedProduct, productResponse);
        productResponse.setCategory(categoryResponse);
        productResponse.setUserResponse(userService.mapEntityToResponse(savedProduct.getUser()));

        return productResponse;
    }
}