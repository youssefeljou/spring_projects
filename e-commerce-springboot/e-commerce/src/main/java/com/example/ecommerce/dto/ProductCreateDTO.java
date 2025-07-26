package com.example.ecommerce.dto;

import com.example.ecommerce.enums.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductCreateDTO {
    @NotNull @NotBlank
    private String name;

    @NotNull //@NotBlank
    private String description;

    @NotNull //@NotBlank
     @DecimalMin(value = "0.1", message = "Price cannot be negative or zero")
    private Float price;

    @NotNull
    private Category category;

    @NotNull @Min(value = 0, message = "Quantity cannot be negative")
    private int stockQuantity;

    @NotNull @NotBlank
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductCreateDTO(String testProduct, String description, double v, int i, String sku123) {
    }

    public ProductCreateDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }


}
