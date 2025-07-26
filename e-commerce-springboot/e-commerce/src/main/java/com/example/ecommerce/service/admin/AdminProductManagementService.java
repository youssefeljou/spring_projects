package com.example.ecommerce.service.admin;

import com.example.ecommerce.dto.ProductAdminDTO;
import com.example.ecommerce.dto.ProductCreateDTO;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.enums.ProductStatus;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class AdminProductManagementService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public AdminProductManagementService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    //----CREATE----
    @Transactional
    public ProductPublicDTO createProduct(@Valid ProductCreateDTO dto) {

        if (productRepository.existsByName(dto.getName())) {
            throw new InvalidInputException(
                    "Product with name '" + dto.getName() + "' already exists. You can update it instead.");
        }

        Product entity = productMapper.toEntity(dto);

        if (entity == null) {
            throw new InvalidInputException("Failed to map product data – please check input arguments.");
        }

        entity.setStatus(ProductStatus.ACTIVE);

        Product saved = productRepository.save(entity);
        return productMapper.toPublicDto(saved);
    }

    //----UPDATE----
    @Transactional
    public ProductPublicDTO updateProduct(Long id, @Valid ProductAdminDTO dto) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("No product found with id " + id);
        }

        Product entity = optionalProduct.get();

        if (!entity.getName().equals(dto.getName())
                && productRepository.existsByName(dto.getName())) {
            throw new InvalidInputException(
                    "Another product already uses the name '" + dto.getName() + "'.");
        }

        productMapper.updateEntity(dto, entity);
        Product saved = productRepository.save(entity);

        return productMapper.toPublicDto(saved);
    }

    //----PATCH (status only)----
    @Transactional
    public void patchStatus(Long id, ProductStatus newStatus) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("No product found with id : " + id);
        }

        Product entity = optionalProduct.get();

        if (entity.getStatus() == newStatus) {
            return;
        }

        entity.setStatus(newStatus);
        productRepository.save(entity);
    }


    //----DELETE (soft delete via @SQLDelete)----
    @Transactional
    public void deleteProduct(Long id) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new ResourceNotFoundException("No product found with id " + id);
        }

        Product entity = optionalProduct.get();
        productRepository.delete(entity);
    }
}
