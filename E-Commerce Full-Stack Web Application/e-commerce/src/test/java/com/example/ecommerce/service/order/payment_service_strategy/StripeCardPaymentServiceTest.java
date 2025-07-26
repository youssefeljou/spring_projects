package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.CreditCardMapper;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.stripe.exception.StripeException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
public class StripeCardPaymentServiceTest {
    @Mock
    private ShoppingCartRepository cartRepo;
    @Mock
    private CustomerRepository custRepo;
    @Mock
    private CreditCardRepository cardRepo;
    @Mock
    private OrderRepository orderRepo;
    @Mock
    private CreditCardMapper mapper;
    @InjectMocks
    private StripeCardPaymentService stripeCardPaymentService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        stripeCardPaymentService = new StripeCardPaymentService(cartRepo, custRepo, cardRepo, orderRepo, mapper);
    }

    @Test
    public void testPay_ShouldProcessPaymentSuccessfully_WithSavedCard() throws StripeException {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(1L, "4242424242424242"
                , "Asala Ahmed", 12, 2026, "132", false);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);
        cart.setTotal(240.55F);

        Customer customer = new Customer();
        customer.setId(1L);

        CreditCard testCard = new CreditCard();
        testCard.setId(1L);
        testCard.setBrand("VISA");

        // Mock repository behaviors
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(custRepo.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));
        when(cardRepo.findById(requestDto.cardId())).thenReturn(Optional.of(testCard));

        // Act
        PaymentResultDto result = stripeCardPaymentService.pay(cartId, requestDto);

        // Assert
        verify(orderRepo, times(1)).save(any(Order.class)); // Ensure order is saved
        verify(cartRepo, times(1)).deleteById(cartId); // Ensure cart is deleted
        verify(custRepo, times(1)).save(any(Customer.class)); // Ensure customer is saved

        assertEquals("DONE", result.getStatus());
        assertEquals("Payment succeeded", result.getMessage());
    }

    @Test
    public void testPay_ShouldProcessPaymentSuccessfully_WithNewCard() throws StripeException {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(null, "4242424242424242"
                , "Asala Ahmed", 12, 2026, "132", false);
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);
        cart.setTotal(240.55F);

        Customer customer = new Customer();
        customer.setId(1L);

        CreditCard testCard = new CreditCard();
        testCard.setId(1L);
        testCard.setBrand("VISA");

        // Mock the behavior of the mapper to return a valid CreditCard object
        CreditCard newCardEntity = new CreditCard();
        newCardEntity.setId(1L);
        when(mapper.toEntity(any())).thenReturn(newCardEntity);

        // Mock repository behaviors
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(custRepo.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));
        when(cardRepo.findById(requestDto.cardId())).thenReturn(Optional.of(testCard));

        // Act
        PaymentResultDto result = stripeCardPaymentService.pay(cartId, requestDto);

        // Assert
        verify(orderRepo, times(1)).save(any(Order.class)); // Ensure order is saved
        verify(cartRepo, times(1)).deleteById(cartId); // Ensure cart is deleted
        verify(custRepo, times(1)).save(any(Customer.class)); // Ensure customer is saved

        assertEquals("DONE", result.getStatus());
        assertEquals("Payment succeeded", result.getMessage());
    }

    @Test
    public void testPay_ShouldThrowInvalidInputException_WhenCartNotFound() {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(1L, "4242424242424242", "Asala Ahmed", 12, 2026, "132", false);

        // Mock the repository behavior to return an empty cart
        when(cartRepo.findById(cartId)).thenReturn(Optional.empty());

        // Act and Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            stripeCardPaymentService.pay(cartId, requestDto);
        });

        assertEquals("Cart not found", thrown.getMessage());
    }

    @Test
    public void testPay_ShouldThrowInvalidInputException_WhenPaymentAlreadyProcessed() {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(1L, "4242424242424242", "Asala Ahmed", 12, 2026, "132", false);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        // Mock repository behavior
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepo.existsByShoppingCart(cart)).thenReturn(true); // Simulate that the payment has already been processed

        // Act and Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            stripeCardPaymentService.pay(cartId, requestDto);
        });

        assertEquals("Payment already processed for this cart.", thrown.getMessage());
    }

    @Test
    public void testPay_ShouldThrowInvalidInputException_WhenCardIsExpired() {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(null, "4242424242424242", "Asala Ahmed", 12, 2021, "132", false);  // Expired card

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock repository behavior
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(custRepo.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));

        // Act and Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            stripeCardPaymentService.pay(cartId, requestDto);
        });

        assertEquals("Card is expired", thrown.getMessage());
    }

    @Test
    public void testPay_ShouldThrowInvalidInputException_WhenCVCIsInvalid() {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(null, "4242424242424242", "Asala Ahmed", 12, 2026, "12", false);  // Invalid CVC

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock repository behavior
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(custRepo.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));

        // Act and Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            stripeCardPaymentService.pay(cartId, requestDto);
        });

        assertEquals("CVV must be 3 digits", thrown.getMessage());
    }

    @Test
    public void testPay_ShouldThrowInvalidInputException_WhenStripeChargeFails() throws StripeException {
        // Arrange
        Long cartId = 1L;
        PaymentRequestDto requestDto = new PaymentRequestDto(null, "4242424242424242", "Asala Ahmed", 12, 2026, "132", true);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setCustomerId(1L);
        cart.setTotal(240.55F);

        Customer customer = new Customer();
        customer.setId(1L);

        // Mock repository behaviors
        when(cartRepo.findById(cartId)).thenReturn(Optional.of(cart));
        when(custRepo.findById(cart.getCustomerId())).thenReturn(Optional.of(customer));

        // Act and Assert
        InvalidInputException thrown = assertThrows(InvalidInputException.class, () -> {
            stripeCardPaymentService.pay(cartId, requestDto);
        });

        assertEquals("Payment processing failed. Please try again or use a different card.", thrown.getMessage());
    }


}
