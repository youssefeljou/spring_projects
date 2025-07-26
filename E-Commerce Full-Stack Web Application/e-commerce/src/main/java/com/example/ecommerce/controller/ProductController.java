package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductFilterDto;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.service.product.ProductCatalogService;
import com.example.ecommerce.service.product.ProductFilterAndSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product APIs", description = "Endpoints for viewing, filtering, and searching products")
public class ProductController {

    private final ProductCatalogService productCatalogService;
    private final ProductFilterAndSearchService productFilterAndSearchService;

    public ProductController(ProductCatalogService productCatalogService,
                             ProductFilterAndSearchService productFilterAndSearchService) {
        this.productCatalogService = productCatalogService;
        this.productFilterAndSearchService = productFilterAndSearchService;
    }

    @Operation(summary = "Get product by ID", description = "Retrieve product details by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public Product getProductById(
            @Parameter(description = "ID of the product to retrieve")
            @PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all available products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(summary = "Search products by name", description = "Search for products using a name keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input parameter"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public Page<ProductPublicDTO> searchProducts(
            @Parameter(description = "Product name or keyword to search for")
            @RequestParam String name,
            @Parameter(description = "Pagination and sorting information")
            @PageableDefault(size = 10, sort = "price") Pageable pageable) {

        return productFilterAndSearchService.searchProductsByName(name, pageable);
    }

    @Operation(summary = "Filter products by category, price range, and other criteria",
            description = "Apply filters such as category and price to narrow down product results")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered products returned successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or missing parameter"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/filter")
    public Page<ProductPublicDTO> filterProducts(
            @RequestBody @Valid ProductFilterDto filter,
            @Parameter(description = "Pagination and sorting information")
            @PageableDefault(size = 10, sort = "price") Pageable pageable) {

        return productFilterAndSearchService.filterProducts(filter, pageable);
    }
}
