package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CreateReviewDto;
import com.example.ecommerce.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "customerId", source = "customer.id")
    CreateReviewDto toDto(Review review);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "customer.id", source = "customerId")
    @Mapping(target = "id", ignore = true) // ignore if it's new
    @Mapping(target = "createdAt", ignore = true) // created at is auto
    Review toEntity(CreateReviewDto dto);
}
