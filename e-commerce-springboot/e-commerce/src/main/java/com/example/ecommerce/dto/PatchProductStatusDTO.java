package com.example.ecommerce.dto;

import com.example.ecommerce.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;

public class PatchProductStatusDTO {
    @NotNull
    private ProductStatus status;

    public PatchProductStatusDTO(String active) {
    }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}

