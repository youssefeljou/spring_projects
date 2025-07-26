package com.example.ecommerce.service.product;

import com.example.ecommerce.dto.ProductFilterDto;
import com.example.ecommerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;

            var dbCategory = cb.lower(cb.function("REPLACE", String.class, root.get("category"), cb.literal(" "), cb.literal("")));
            return cb.like(dbCategory, "%" + category.toLowerCase() + "%");
        };
    }

    public static Specification<Product> priceGreaterThanOrEqual(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.ge(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqual(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.le(root.get("price"), maxPrice);
    }

    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> nameOrCategoryContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;

            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("category").as(String.class)), pattern)
            );
        };
    }

    public static Specification<Product> withFilters(ProductFilterDto filter) {
        if (filter == null) {
            return null; // no filtering
        }

        Specification<Product> spec = null;

        if (filter.getMinPrice() != null) {
            spec = combine(spec, priceGreaterThanOrEqual(filter.getMinPrice()));
        }

        if (filter.getMaxPrice() != null) {
            spec = combine(spec, priceLessThanOrEqual(filter.getMaxPrice()));
        }

        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            spec = combine(spec, hasCategory(filter.getCategory()));
        }

        return spec;
    }

    // Helper to combine specs safely
    private static Specification<Product> combine(Specification<Product> base, Specification<Product> toAdd) {
        if (base == null) {
            return toAdd;
        }
        if (toAdd == null) {
            return base;
        }
        return base.and(toAdd);
    }
}
