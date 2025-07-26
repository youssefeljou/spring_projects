package com.example.ecommerce.service.product;


import com.example.ecommerce.dto.ProductFilterDto;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductFilterAndSearchService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductPublicDTO> filterProducts(ProductFilterDto filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.withFilters(filter);
        // spec might be null which is safe to pass to findAll() -> returns all
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toPublicDto);
    }

    public Page<ProductPublicDTO> searchProductsByName(String name, Pageable pageable) {
        Specification<Product> spec = (name == null || name.isBlank()) ? null
                : ProductSpecifications.nameOrCategoryContains(name);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toPublicDto);
    }
}

/***
 * auto-suggestion and matching
 */
