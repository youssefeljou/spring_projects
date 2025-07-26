package com.example.ecommerce.service.admin;

import com.example.ecommerce.dto.ProductAdminDTO;
import com.example.ecommerce.dto.ProductCreateDTO;
import com.example.ecommerce.dto.ProductPublicDTO;
import com.example.ecommerce.enums.Category;
import com.example.ecommerce.enums.ProductStatus;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminProductManagementServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private AdminProductManagementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------- CREATE -----------

    @Test
    void createProduct_shouldReturnDto_whenValidInput() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Bluetooth Speaker");
        dto.setDescription("Waterproof speaker with bass and 12h playtime.");
        dto.setPrice(850.0f);
        dto.setCategory(Category.ELECTRONICS);
        dto.setStockQuantity(40);
        dto.setImage("https://m.media-amazon.com/images/I/71D9ImsvEtL._AC_SY879_.jpg"); // ✅ String URL

        Product entity = new Product();
        Product saved = new Product();
        saved.setStatus(ProductStatus.ACTIVE);
        saved.setImage(dto.getImage()); // ✅ Match test input

        ProductPublicDTO publicDTO = new ProductPublicDTO();

        when(productRepository.existsByName("Bluetooth Speaker")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(productMapper.toPublicDto(saved)).thenReturn(publicDTO);

        ProductPublicDTO result = service.createProduct(dto);

        assertNotNull(result);
        verify(productRepository).save(entity);
    }

    @Test
    void createProduct_shouldThrowException_whenNameExists() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Fashion Watch");

        when(productRepository.existsByName("Fashion Watch")).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> service.createProduct(dto));
    }

    // ----------- UPDATE -----------

    @Test
    void updateProduct_shouldReturnDto_whenValidInput() {
        Long id = 1L;

        ProductAdminDTO dto = new ProductAdminDTO();
        dto.setName("Wireless Earbuds");
        dto.setDescription("Noise-cancelling earbuds");
        dto.setPrice(250.0f);
        dto.setCategory(Category.ELECTRONICS);
        dto.setStockQuantity(50);
        dto.setStatus(ProductStatus.ACTIVE);

        Product existingProduct = new Product();
        existingProduct.setName("Old Earbuds");

        Product saved = new Product();
        ProductPublicDTO publicDTO = new ProductPublicDTO();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("Wireless Earbuds")).thenReturn(false);
        doNothing().when(productMapper).updateEntity(dto, existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(saved);
        when(productMapper.toPublicDto(saved)).thenReturn(publicDTO);

        ProductPublicDTO result = service.updateProduct(id, dto);

        assertNotNull(result);
        verify(productMapper).updateEntity(dto, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_shouldThrowException_whenProductNotFound() {
        Long id = 404L;
        ProductAdminDTO dto = new ProductAdminDTO();
        dto.setName("Unknown");

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateProduct(id, dto));
    }

    @Test
    void updateProduct_shouldThrowException_whenNameAlreadyUsedByAnotherProduct() {
        Long id = 5L;
        ProductAdminDTO dto = new ProductAdminDTO();
        dto.setName("ELECTRONIC HUB");

        Product existingProduct = new Product();
        existingProduct.setName("Different Name");

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName("ELECTRONIC HUB")).thenReturn(true);

        assertThrows(InvalidInputException.class, () -> service.updateProduct(id, dto));
    }

    // ----------- PATCH STATUS -----------

    @Test
    void patchStatus_shouldUpdateStatus_whenDifferent() {
        Long id = 7L;
        Product product = new Product();
        product.setStatus(ProductStatus.DISCONTINUED);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        service.patchStatus(id, ProductStatus.ACTIVE);

        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void patchStatus_shouldDoNothing_whenStatusIsSame() {
        Long id = 8L;
        Product product = new Product();
        product.setStatus(ProductStatus.ACTIVE);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        service.patchStatus(id, ProductStatus.ACTIVE);

        verify(productRepository, never()).save(any());
    }

    @Test
    void patchStatus_shouldThrowException_whenProductNotFound() {
        Long id = 999L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.patchStatus(id, ProductStatus.DISCONTINUED));
    }

    // ----------- DELETE -----------

    @Test
    void deleteProduct_shouldDelete_whenProductExists() {
        Long id = 10L;
        Product product = new Product();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        service.deleteProduct(id);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteProduct(404L));
    }
    @Test
    void createProduct_shouldThrowException_whenMapperReturnsNull() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Test Product");

        when(productRepository.existsByName("Test Product")).thenReturn(false);
        when(productMapper.toEntity(dto)).thenReturn(null);

        assertThrows(InvalidInputException.class, () -> service.createProduct(dto));
    }
}
