package com.example.ecommerce.service.shoppingcart;

import com.example.ecommerce.dto.AddToCartRequestDto;
import com.example.ecommerce.dto.UpdateCartRequestDto;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingCartServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks private ShoppingCartService shoppingCartService;

    private final float TAX_RATE = 0.14f;

    @BeforeEach
    void setUp() throws Exception {
        customerRepository = mock(CustomerRepository.class);
        productRepository = mock(ProductRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        shoppingCartRepository = mock(ShoppingCartRepository.class);

        shoppingCartService = new ShoppingCartService(
                customerRepository,
                productRepository,
                cartItemRepository,
                shoppingCartRepository
        );

        // Set taxRate manually using reflection since @Value doesn't work in unit tests
        Field taxRateField = ShoppingCartService.class.getDeclaredField("taxRate");
        taxRateField.setAccessible(true);
        taxRateField.set(shoppingCartService, TAX_RATE);
    }

    @Test
    void addProductToCart_shouldAddNewItem_whenCartExists() {
        Long customerId = 1L;
        Long productId = 10L;

        AddToCartRequestDto request = new AddToCartRequestDto(productId, 2);

        Product product = new Product();
        product.setId(productId);
        product.setPrice(100f);
        product.setStockQuantity(10);

        Customer customer = new Customer();
        customer.setId(customerId);
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new ArrayList<>());
        customer.setShoppingCart(cart);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(shoppingCartRepository.save(any())).thenReturn(cart);

        shoppingCartService.addProductToCart(customerId, request);

        assertEquals(1, cart.getItems().size());
        assertEquals(228f, cart.getTotal()); // 2*100=200 + 14% tax = 228
    }

    @Test
    void addProductToCart_shouldThrow_whenProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingCartService.addProductToCart(1L, new AddToCartRequestDto(5L, 1)));
    }

    @Test
    void addProductToCart_shouldThrow_whenStockNotAvailable() {
        Product p = new Product();
        p.setStockQuantity(1);
        p.setId(5L);

        when(productRepository.findById(5L)).thenReturn(Optional.of(p));

        assertThrows(InvalidInputException.class,
                () -> shoppingCartService.addProductToCart(1L, new AddToCartRequestDto(5L, 2)));
    }

    @Test
    void updateCart_shouldUpdateQuantity_whenValid() {
        Long customerId = 1L;
        Long productId = 10L;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);
        product.setPrice(50f);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setShoppingCart(cart);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        UpdateCartRequestDto request = new UpdateCartRequestDto(productId, 3);
        shoppingCartService.updateCart(customerId, request);

        assertEquals(3, cartItem.getQuantity());
    }

    @Test
    void updateCart_shouldThrow_whenQuantityNegative() {
        assertThrows(ResourceNotFoundException.class,
                () -> shoppingCartService.updateCart(1L, new UpdateCartRequestDto(1L, -5)));
    }

    @Test
    void updateCart_shouldThrow_whenCustomerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingCartService.updateCart(1L, new UpdateCartRequestDto(1L, 1)));
    }

    @Test
    void deleteProductFromCart_shouldRemoveItem_whenQuantityIsOne() {
        Long customerId = 1L;
        Long productId = 2L;

        Product product = new Product();
        product.setId(productId);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        Customer customer = new Customer();
        customer.setShoppingCart(cart);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        shoppingCartService.deleteProductFromCart(customerId, productId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void deleteProductFromCart_shouldThrow_whenCartIsEmpty() {
        Customer customer = new Customer();
        customer.setShoppingCart(new ShoppingCart());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(InvalidInputException.class,
                () -> shoppingCartService.deleteProductFromCart(1L, 1L));
    }

    @Test
    void clearCart_shouldClearAndUnlinkCart() {
        Long cartId = 5L;

        Customer customer = new Customer();
        customer.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setItems(List.of(new CartItem()));

        customer.setShoppingCart(cart);

        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(customerRepository.findByShoppingCart_Id(cartId)).thenReturn(Optional.of(customer));

        shoppingCartService.clearCart(cartId);

        verify(shoppingCartRepository).deleteById(cartId);
        verify(customerRepository).save(customer);

        assertNull(customer.getShoppingCart());
    }

    @Test
    void removeProductCompletelyFromCart_shouldDeleteItem() {
        Long customerId = 1L;
        Long productId = 10L;

        Product product = new Product();
        product.setId(productId);

        CartItem item = new CartItem();
        item.setProduct(product);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new ArrayList<>(List.of(item)));

        Customer customer = new Customer();
        customer.setShoppingCart(cart);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        shoppingCartService.removeProductCompletelyFromCart(customerId, productId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartItemRepository).delete(item);
    }

    @Test
    void updateCart_shouldThrow_whenQuantityIsSame() {
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(3);

        ShoppingCart cart = new ShoppingCart();
        cart.setItems(List.of(cartItem));

        Customer customer = new Customer();
        customer.setShoppingCart(cart);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InvalidInputException.class,
                () -> shoppingCartService.updateCart(1L, new UpdateCartRequestDto(1L, 3)));
    }
}
