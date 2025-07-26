package com.example.ecommerce.service.product;

import com.example.ecommerce.dto.ProductFilterDto;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductFilterAndSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductFilterAndSearchService productFilterAndSearchService;

    private Product product;
    private Product product2;
    private ProductPublicDTO productPublicDTO;
    private ProductPublicDTO productPublicDTO2;

    private Pageable pageable;
    private Validator validator;

    @BeforeEach
    void setUp() {
        product = new Product();
        product2 = new Product();

        productPublicDTO = new ProductPublicDTO();
        productPublicDTO.setName("Test Product");
        productPublicDTO.setPrice(10.0f);

        productPublicDTO2 = new ProductPublicDTO();
        productPublicDTO2.setName("Another Product");
        productPublicDTO2.setPrice(20.0f);

        pageable = PageRequest.of(0, 10);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testFilterProductsReturnsMappedPage() {
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setCategory("Electronics");
        filterDto.setMinPrice(5.0);
        filterDto.setMaxPrice(50.0);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);

        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());

        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
    }

    @Test
    void testFilterProductsWithNullFilter() {
        Page<Product> emptyPage = Page.empty(pageable);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(emptyPage);

        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(null, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verifyNoInteractions(productMapper);
    }

    @Test
    void testFilterProductsWithBlankCategory() {
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setCategory("   ");
        filterDto.setMinPrice(5.0);
        filterDto.setMaxPrice(50.0);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);

        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
    }

    @Test
    void testFilterProductsWithMinPriceGreaterThanMaxPrice() {
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setCategory("Electronics");
        filterDto.setMinPrice(100.0);
        filterDto.setMaxPrice(50.0);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);

        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
    }

    @Test
    void testFilterProductsWithPartialCategoryMatch() {
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setCategory("ele");
        filterDto.setMinPrice(5.0);
        filterDto.setMaxPrice(50.0);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);

        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());

        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
    }

    @Test
    void testSearchProductsByNameReturnsMappedPage() {
        String searchName = "phone";
        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);

        Page<ProductPublicDTO> result = productFilterAndSearchService.searchProductsByName(searchName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());

        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
    }

    @Test
    void testSearchProductsByNameWithNullOrBlank() {
        Page<Product> emptyPage = Page.empty(pageable);
        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(emptyPage);

        Page<ProductPublicDTO> nullResult = productFilterAndSearchService.searchProductsByName(null, pageable);
        assertNotNull(nullResult);
        assertEquals(0, nullResult.getTotalElements());

        Page<ProductPublicDTO> blankResult = productFilterAndSearchService.searchProductsByName("   ", pageable);
        assertNotNull(blankResult);
        assertEquals(0, blankResult.getTotalElements());

        verify(productRepository, times(2)).findAll((Specification<Product>) any(), eq(pageable));
        verifyNoInteractions(productMapper);
    }

    @Test
    void testFilterProductsWithMultipleProducts() {
        List<Product> products = List.of(product, product2);
        List<ProductPublicDTO> dtos = List.of(productPublicDTO, productPublicDTO2);
        Page<Product> productPage = new PageImpl<>(products, pageable, 2);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageable))).thenReturn(productPage);
        when(productMapper.toPublicDto(product)).thenReturn(productPublicDTO);
        when(productMapper.toPublicDto(product2)).thenReturn(productPublicDTO2);

        ProductFilterDto filterDto = new ProductFilterDto();
        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
        assertEquals("Another Product", result.getContent().get(1).getName());

        verify(productRepository).findAll((Specification<Product>) any(), eq(pageable));
        verify(productMapper).toPublicDto(product);
        verify(productMapper).toPublicDto(product2);
    }

    @Test
    void testPaginationMetadata() {
        Product product1 = new Product();
        Product product2 = new Product();

        ProductPublicDTO dto1 = new ProductPublicDTO();
        dto1.setName("Product 1");
        ProductPublicDTO dto2 = new ProductPublicDTO();
        dto2.setName("Product 2");

        List<Product> products = List.of(product1, product2);
        int pageSize = 5;
        int pageNumber = 2;
        long totalElements = 12;

        Pageable pageableCustom = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = new PageImpl<>(products, pageableCustom, totalElements);

        when(productRepository.findAll((Specification<Product>) any(), eq(pageableCustom))).thenReturn(productPage);
        when(productMapper.toPublicDto(product1)).thenReturn(dto1);
        when(productMapper.toPublicDto(product2)).thenReturn(dto2);

        ProductFilterDto filterDto = new ProductFilterDto();
        Page<ProductPublicDTO> result = productFilterAndSearchService.filterProducts(filterDto, pageableCustom);

        assertEquals(pageNumber, result.getNumber());
        assertEquals(pageSize, result.getSize());
        assertEquals(totalElements, result.getTotalElements());

        int expectedTotalPages = (int) Math.ceil((double) totalElements / pageSize);
        assertEquals(expectedTotalPages, result.getTotalPages());

        assertEquals(products.size(), result.getContent().size());
        assertEquals(dto1.getName(), result.getContent().get(0).getName());
        assertEquals(dto2.getName(), result.getContent().get(1).getName());
    }

    @Test
    void testBlankCategoryFailsValidation() {
        ProductFilterDto dto = new ProductFilterDto();
        dto.setCategory("  ");
        dto.setMinPrice(10.0);
        dto.setMaxPrice(100.0);
        Set<ConstraintViolation<ProductFilterDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
    }

    @Test
    void testMinGreaterThanMaxFailsValidation() {
        ProductFilterDto dto = new ProductFilterDto();
        dto.setCategory("Electronics");
        dto.setMinPrice(100.0);
        dto.setMaxPrice(10.0);

        Set<ConstraintViolation<ProductFilterDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("validPriceRange") &&
                        v.getMessage().contains("Minimum price must be less than or equal to maximum price")));
    }

    @Test
    void testValidDtoPassesValidation() {
        ProductFilterDto dto = new ProductFilterDto();
        dto.setCategory("Electronics");
        dto.setMinPrice(10.0);
        dto.setMaxPrice(100.0);

        Set<ConstraintViolation<ProductFilterDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}
