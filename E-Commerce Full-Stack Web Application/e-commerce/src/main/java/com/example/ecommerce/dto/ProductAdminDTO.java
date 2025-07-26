package com.example.ecommerce.dto;

import com.example.ecommerce.enums.Category;
import com.example.ecommerce.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ProductAdminDTO {
    @NotNull
    @NotBlank
    private String name;
    @NotNull @NotBlank
    private String description;
    @NotNull @NotBlank @DecimalMin(value = "0.1",message = "Price can not be zero or negative")
    private Float price;
    @NotNull @NotBlank
    private Category category;
    @NotNull @NotBlank @Min(value=1,message = "Quantity can not be a negative number")
    private int stockQuantity;
    @NotNull @NotBlank
    private ProductStatus status;

    private Float rate; //derived

    public ProductAdminDTO(String updatedProduct, String updatedDesc, double v, int i, String s, boolean b) {
    }

    public ProductAdminDTO() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }
}
