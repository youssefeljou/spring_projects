package com.example.ecommerce.service.customer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.mapper.CreditCardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.Optional;
import java.util.List;

public class CreditCardServiceTest {

    @Mock
    private CreditCardRepository cardRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private CreditCardMapper mapper;

    @InjectMocks
    private CreditCardService creditCardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        creditCardService = new CreditCardService(cardRepo, customerRepo, mapper);
    }

    // Test: addCard
    @Test
    void addCard_shouldAddCardSuccessfully() {
        Long customerId = 1L;
        CreditCardCreateDTO dto = new CreditCardCreateDTO("1234567890123456", "John Doe", "08/25");

        Customer customer = new Customer();
        when(customerRepo.getReferenceById(customerId)).thenReturn(customer);

        // Create a real CreditCard object and spy on it
        CreditCard card = new CreditCard();
        card.setExpiration(YearMonth.of(2025, 8)); // Set expiration date to future

        CreditCard spyCard = spy(card);  // Create a spy to mock methods
        when(spyCard.isExpired()).thenReturn(false);  // Mock isExpired() method to return false

        when(mapper.toEntity(dto)).thenReturn(spyCard);
        when(mapper.toDto(spyCard)).thenReturn(new CreditCardViewDTO(1L, "3456", "Visa", "John Doe", YearMonth.of(2025, 8)));

        CreditCardViewDTO result = creditCardService.addCard(customerId, dto);

        verify(cardRepo).save(spyCard);
        assertNotNull(result);
        assertEquals("3456", result.last4());
    }




    @Test
    void addCard_shouldThrowExceptionWhenCardIsExpired() {
        Long customerId = 1L;
        CreditCardCreateDTO dto = new CreditCardCreateDTO("1234567890123456", "John Doe", "01/22");

        Customer customer = new Customer();
        when(customerRepo.getReferenceById(customerId)).thenReturn(customer);

        CreditCard card = new CreditCard();
        when(mapper.toEntity(dto)).thenReturn(card);
        card.setExpiration(YearMonth.now().minusMonths(1)); // Expired card

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            creditCardService.addCard(customerId, dto);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Card expired", exception.getReason());
    }

    // Test: listCards
    @Test
    void listCards_shouldReturnCardList() {
        Long customerId = 1L;
        List<CreditCard> cards = List.of(new CreditCard(), new CreditCard());
        when(cardRepo.findByCustomerId(customerId)).thenReturn(cards);
        when(mapper.toDto(any(CreditCard.class))).thenReturn(new CreditCardViewDTO(1L, "3456", "Visa", "John Doe", YearMonth.of(2025, 1)));

        List<CreditCardViewDTO> result = creditCardService.listCards(customerId);

        assertEquals(2, result.size());
    }

    // Test: getCard
    @Test
    void getCard_shouldReturnCard() {
        Long customerId = 1L;
        Long cardId = 1L;
        CreditCard card = new CreditCard();
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.of(card));
        when(mapper.toDto(card)).thenReturn(new CreditCardViewDTO(1L, "3456", "Visa", "John Doe", YearMonth.of(2025, 1)));

        CreditCardViewDTO result = creditCardService.getCard(customerId, cardId);

        assertNotNull(result);
    }

    @Test
    void getCard_shouldThrowNotFoundException() {
        Long customerId = 1L;
        Long cardId = 1L;
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            creditCardService.getCard(customerId, cardId);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Card not found", exception.getReason());
    }

    // Test: updateCard
    @Test
    void updateCard_shouldUpdateCardSuccessfully() {
        Long customerId = 1L;
        Long cardId = 1L;
        CreditCardUpdateDTO dto = new CreditCardUpdateDTO("John Doe", "01/27");

        // Create a CreditCard instance with initial values
        CreditCard card = new CreditCard();
        card.setCardHolderName("Old Name"); // Initial value for testing

        // Mock the repository call to return the card
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.of(card));

        // Mock the mapper.toDto() to return a CreditCardViewDTO
        CreditCardViewDTO expectedDto = new CreditCardViewDTO(1L, "3456", "Visa", "John Doe", YearMonth.of(2027, 1));
        when(mapper.toDto(card)).thenReturn(expectedDto);

        // Call the updateCard method
        CreditCardViewDTO result = creditCardService.updateCard(customerId, cardId, dto);

        // Assertions
        assertNotNull(result);  // Ensure result is not null
        assertEquals("John Doe", card.getCardHolderName());  // Verify card holder name was updated
        assertEquals(YearMonth.parse("2027-01"), card.getExpiration());  // Verify expiration date was updated
        assertEquals(expectedDto, result);  // Verify the returned DTO matches the expected result
    }


    @Test
    void updateCard_shouldThrowExceptionWhenCardIsExpired() {
        Long customerId = 1L;
        Long cardId = 1L;
        CreditCardUpdateDTO dto = new CreditCardUpdateDTO("John Doe", "01/20");
        CreditCard card = new CreditCard();
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.of(card));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            creditCardService.updateCard(customerId, cardId, dto);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Card expired", exception.getReason());
    }

    // Test: deleteCard
    @Test
    void deleteCard_shouldDeleteCardSuccessfully() {
        Long customerId = 1L;
        Long cardId = 1L;
        CreditCard card = new CreditCard();
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.of(card));

        creditCardService.deleteCard(customerId, cardId);

        verify(cardRepo).delete(card);
    }

    @Test
    void deleteCard_shouldThrowNotFoundException() {
        Long customerId = 1L;
        Long cardId = 1L;
        when(cardRepo.findByIdAndCustomerId(cardId, customerId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            creditCardService.deleteCard(customerId, cardId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Card not found", exception.getReason());
    }
}
