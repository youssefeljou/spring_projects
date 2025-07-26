package com.example.ecommerce.repository;

import com.example.ecommerce.enums.ProductStatus;
import com.example.ecommerce.model.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByStatus(ProductStatus status);


    boolean existsByName(String name);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
