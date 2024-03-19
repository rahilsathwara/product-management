package com.spring.task.payload.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductRequest {

    @NotEmpty(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @NotEmpty(message = "Description is required")
    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @Positive(message = "Price must be positive")
    private float price;

    @Positive(message = "Weight must be positive")
    private float weight;

    @NotEmpty(message = "Weight unit is required")
    private String weightUnit;

    @NotEmpty(message = "Brand is required")
    @Size(max = 255, message = "Brand must be at most 255 characters")
    private String brand;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private LocalDateTime expiryDate;

    @NotEmpty(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "Inventory is required")
    private String inventory;
}