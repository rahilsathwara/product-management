package com.spring.task.service.serviceimpl;

import com.spring.task.entity.Category;
import com.spring.task.exception.ResourceAlreadyExistException;
import com.spring.task.exception.ResourceNotFoundException;
import com.spring.task.payload.request.CategoryRequest;
import com.spring.task.payload.response.CategoryResponse;
import com.spring.task.repository.CategoryRepository;
import com.spring.task.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {
        logger.info("Creating category: {}", categoryRequest.getName());
        // validate category exist or not
        categoryRepository.findByNameIgnoreCase(categoryRequest.getName())
                .ifPresent(role -> {
                    logger.error("Category already exists: " + categoryRequest.getName());
                    throw new ResourceAlreadyExistException("Category already exist" + categoryRequest.getName());
                });

        Category newCategory = new Category();
        BeanUtils.copyProperties(categoryRequest, newCategory);
        newCategory.setUpdatedAt(LocalDateTime.now());
        newCategory.setCreatedAt(LocalDateTime.now());

        return categoryRepository.save(newCategory);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<CategoryResponse> getAllCategoriesLst() {
        return categoryRepository.findAll().stream()
                .map(this::mapEntityToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Category category = getCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with Id : " + id));

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        logger.info("Updating category with ID: {}", id);

        // check category exist or not
        Category category = getCategoryById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with ID: " + id);
                    return  new ResourceNotFoundException("Category not found with Id : " + id);
                });

        // check category name exist or not
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryRequest.getName(), id)) {
            logger.error("Category with name " + categoryRequest.getName() + " already exists");
            throw new ResourceAlreadyExistException("Category with name " + categoryRequest.getName() + " already exists");
        }

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        category.setImageUrl(categoryRequest.getImageUrl());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    @Override
    public CategoryResponse mapEntityToResponse(Category savedCategory) {
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(savedCategory, response);

        return response;
    }
}