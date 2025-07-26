package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;
import com.example.ecommerce.exception.DuplicateOrderException;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CashPaymentServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CashPaymentService cashPaymentService;

    private final PaymentRequestDto mockPaymentRequest = new PaymentRequestDto(null,null,null
            ,null,null,null,false);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cashPaymentService = new CashPaymentService(orderRepository, shoppingCartRepository, customerRepository);
    }

    @Test
    public void testPay_ShouldPlaceOrderSuccessfully() {
        // Setup mock data
        Long cartId = 1L;


        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock repository behavior
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(customerRepository.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));

        // Call the method
        PaymentResultDto result = cashPaymentService.pay(cartId, mockPaymentRequest);

        // Assertions
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(shoppingCartRepository, times(1)).deleteById(cartId);
        verify(customerRepository, times(1)).save(any(Customer.class));

        assertEquals("DONE", result.getStatus());
        assertEquals("Cash payment confirmed. Order placed successfully.", result.getMessage());
    }

    @Test
    public void testPay_ShouldThrowIllegalArgumentException_WhenShoppingCartNotFound() {
        Long cartId = 1L;

        // Mock repository behavior
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Call the method and assert exception
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cashPaymentService.pay(cartId, mockPaymentRequest);
        });

        assertEquals("Shopping cart not found with ID: 1", thrown.getMessage());
    }

    @Test
    public void testPay_ShouldThrowIllegalArgumentException_WhenCustomerNotFound() {
        Long cartId = 1L;

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        // Mock repository behavior
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(customerRepository.findById(cart.getCustomerId())).thenReturn(Optional.empty());

        // Call the method and assert exception
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cashPaymentService.pay(cartId, mockPaymentRequest);
        });

        assertEquals("Customer not found with ID: 1", thrown.getMessage());
    }


    @Test
    public void testPay_ShouldThrowDuplicateOrderException_WhenDuplicateOrderOccurs() {
        Long cartId = 1L;

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock repository behavior
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(customerRepository.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));

        // Simulate duplicate order exception
        DataIntegrityViolationException dataIntegrityException = new DataIntegrityViolationException("orders_shopping_cart_id_key");
        doThrow(dataIntegrityException).when(orderRepository).save(any(Order.class));

        // Call the method and assert exception
        DuplicateOrderException thrown = assertThrows(DuplicateOrderException.class, () -> {
            cashPaymentService.pay(cartId, mockPaymentRequest);
        });

        assertEquals("The order has already been paid.", thrown.getMessage());
    }
}
