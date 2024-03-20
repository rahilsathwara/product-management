package com.spring.task.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private float price;
    private float weight;
    private String weightUnit;
    private String brand;
    private CategoryResponse category;
    private LocalDateTime expiryDate;
    private UserResponse userResponse;
    private int inventory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}