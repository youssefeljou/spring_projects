package com.example.ecommerce.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class ProductFilterDto {

    @NotBlank(message = "Category must not be blank")
    private String category;

    private Double minPrice;
    private Double maxPrice;

    public ProductFilterDto() {
    }

    public ProductFilterDto(String category, Double minPrice, Double maxPrice) {
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @AssertTrue(message = "Minimum price must be less than or equal to maximum price")
    public boolean isValidPriceRange() {
        return minPrice == null || maxPrice == null || minPrice <= maxPrice;
    }

    public String getCategory() {
        if (category == null) return null;
        return category.replaceAll("\\s+", "").toLowerCase(); // remove all spaces and lowercase
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
}
