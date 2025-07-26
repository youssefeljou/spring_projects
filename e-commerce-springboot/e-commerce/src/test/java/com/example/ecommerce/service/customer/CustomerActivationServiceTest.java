package com.example.ecommerce.service.customer;

import com.example.ecommerce.dto.VerificationDto;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.VerificationToken;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.VerificationTokenRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerActivationServiceTest {

    @InjectMocks
    private CustomerActivationService customerActivationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testVerifyToken_Success() {
        String email = "user@example.com";
        String code = "123456";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setAccountStatus(Status.DEACTIVATED);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(customer);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.of(token));

        String result = customerActivationService.verifyToken(dto);

        assertEquals("Account verified successfully!", result);
        assertEquals(Status.ACTIVATED, customer.getAccountStatus());
        verify(customerRepository).save(customer);
        verify(tokenRepository).delete(token);
    }

    @Test
    void testVerifyToken_CustomerNotFound() {
        VerificationDto dto = new VerificationDto("notfound@example.com", "token");
        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testVerifyToken_TokenNotFound() {
        String email = "user@example.com";
        String code = "wrong-token";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Invalid or mismatched token", ex.getMessage());
    }

    @Test
    void testVerifyToken_TokenExpired() {
        String email = "user@example.com";
        String code = "expired";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(customer);
        token.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Token expired", ex.getMessage());
        verify(tokenRepository).delete(token);
    }

    @Test
    void testVerifyToken_TokenBelongsToAnotherUser() {
        String email = "user1@example.com";
        String code = "validToken";
        VerificationDto dto = new VerificationDto(email, code);

        Customer user1 = new Customer();
        user1.setEmail(email);

        Customer user2 = new Customer();
        user2.setEmail("user2@example.com");

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(user2);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(tokenRepository.findByTokenAndCustomer(code, user1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Invalid or mismatched token", ex.getMessage());
    }

    @Test
    void testRemoveExpiredTokens() {
        customerActivationService.removeExpiredTokens();
        verify(tokenRepository).deleteAllByExpiryDateBefore(any(LocalDateTime.class));
    }

    @Test
    void testVerifyToken_NullEmail() {
        VerificationDto dto = new VerificationDto(null, "code");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testVerifyToken_BlankEmail() {
        VerificationDto dto = new VerificationDto("   ", "code");

        when(customerRepository.findByEmail(dto.getEmail().trim()))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testVerifyToken_NullToken() {
        VerificationDto dto = new VerificationDto("user@example.com", null);

        Customer customer = new Customer();
        customer.setEmail("user@example.com");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(null, customer)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Invalid or mismatched token", ex.getMessage());
    }

    @Test
    void testVerifyToken_BlankToken() {
        VerificationDto dto = new VerificationDto("user@example.com", "   ");

        Customer customer = new Customer();
        customer.setEmail("user@example.com");

        when(customerRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(dto.getCode(), customer)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Invalid or mismatched token", ex.getMessage());
    }

    @Test
    void testVerifyToken_ExactExpiryTime() {
        String email = "user@example.com";
        String code = "tokenExactNow";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(customer);
        token.setExpiryDate(LocalDateTime.now());

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Token expired", ex.getMessage());
        verify(tokenRepository).delete(token);
    }

    @Test
    void testVerifyToken_MultipleTokensSameUser() {
        String email = "user@example.com";
        String code = "token1";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(customer);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        // simulate that only one token is picked even if multiple exist//
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.of(token));

        String result = customerActivationService.verifyToken(dto);

        assertEquals("Account verified successfully!", result);
        assertEquals(Status.ACTIVATED, customer.getAccountStatus());
    }

    @Test
    void testVerifyToken_ReusedTokenAfterDeletion() {
        String email = "user@example.com";
        String code = "usedToken";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("Invalid or mismatched token", ex.getMessage());
    }

    @Test
    void testVerifyToken_DBFailureOnSave() {
        String email = "user@example.com";
        String code = "123456";
        VerificationDto dto = new VerificationDto(email, code);

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setAccountStatus(Status.DEACTIVATED);

        VerificationToken token = new VerificationToken();
        token.setToken(code);
        token.setCustomer(customer);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(tokenRepository.findByTokenAndCustomer(code, customer)).thenReturn(Optional.of(token));
        doThrow(new RuntimeException("DB down")).when(customerRepository).save(any(Customer.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("DB down", ex.getMessage());
    }

    @Test
    void testVerifyToken_XSSInjection() {
        String email = "<script>alert(1)</script>@example.com";
        String code = "<script>alert(2)</script>";
        VerificationDto dto = new VerificationDto(email, code);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                customerActivationService.verifyToken(dto));
        assertEquals("User not found", ex.getMessage());
    }
}
