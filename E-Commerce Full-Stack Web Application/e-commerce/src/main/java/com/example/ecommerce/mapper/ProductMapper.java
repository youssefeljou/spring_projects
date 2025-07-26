// src/main/java/com/example/ecommerce/mapper/ProductMapper.java
package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.product.ProductReviewService;
import jakarta.validation.Valid;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = ProductReviewService.class)
public abstract class ProductMapper {

    @Autowired
    protected ProductReviewService reviewService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract Product toEntity(ProductCreateDTO dto);

    @Mapping(target = "rate", expression = "java(reviewService.calculateAverageRating(product.getId()))")
    public abstract ProductAdminDTO toAdminDto(Product product);

    @Mapping(target = "rate", expression = "java(reviewService.calculateAverageRating(product.getId()))")
    public abstract ProductPublicDTO toPublicDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    public abstract void updateEntity(@Valid ProductAdminDTO dto, @MappingTarget Product entity);
}
