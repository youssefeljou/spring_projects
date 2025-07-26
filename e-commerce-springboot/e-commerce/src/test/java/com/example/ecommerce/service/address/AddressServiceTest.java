package com.example.ecommerce.service.address;

import com.example.ecommerce.dto.AddressDto;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.util.IdOwnership;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private HttpSession session;

    @Mock
    private IdOwnership idOwnership; // Mock IdOwnership

    @Mock
    private AddressMapper addressMapper; // Mock AddressMapper

    @InjectMocks
    private AddressService addressService;

    private final Long customerId = 1L;

    private AddressDto sampleAddressDto;
    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample AddressDto setup
        sampleAddressDto = new AddressDto();
        sampleAddressDto.setId(1L);
        sampleAddressDto.setCountry("Egypt");
        sampleAddressDto.setCity("Cairo");
        sampleAddressDto.setStreetName("Tahrir St.");
        sampleAddressDto.setHouseNumber(10);
        sampleAddressDto.setApartmentNumber(3);
        sampleAddressDto.setPostCode("12345");

        // Sample Address setup (this mimics the entity model that will be saved in the DB)
        sampleAddress = new Address();
        sampleAddress.setId(1L);
        sampleAddress.setCountry("Egypt");
        sampleAddress.setCity("Cairo");
        sampleAddress.setStreetName("Tahrir St.");
        sampleAddress.setHouseNumber(10);
        sampleAddress.setApartmentNumber(3);
        sampleAddress.setPostCode("12345");
    }

    // ----------- GET ALL -----------

    @Test
    void getAddressesForCustomer_shouldReturnList_whenValid() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByCustomerId(customerId)).thenReturn(List.of(sampleAddress));

        // Mock the mapping from Address to AddressDto using AddressMapper
        when(addressMapper.toDto(sampleAddress)).thenReturn(sampleAddressDto);

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        List<AddressDto> response = addressService.getAddressesForCustomer(customerId, session);

        assertEquals(1, response.size());
        assertEquals("Cairo", response.get(0).getCity()); // check that the city matches
    }

    // ----------- GET ONE -----------

    @Test
    void getAddress_shouldReturnAddress_whenFound() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(1L, customerId)).thenReturn(Optional.of(sampleAddress));

        // Mock the mapping
        when(addressMapper.toDto(sampleAddress)).thenReturn(sampleAddressDto);

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        AddressDto response = addressService.getAddress(customerId, 1L, session);

        assertEquals("Cairo", response.getCity()); // validate that the city is correct
    }

    @Test
    void getAddress_shouldThrow_whenAddressNotFound() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(2L, customerId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> addressService.getAddress(customerId, 2L, session));
    }

    // ----------- CREATE -----------

    @Test
    void createAddress_shouldCreate_whenValid() {
        Customer customer = new Customer();
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Mock the mapping and saving
        when(addressMapper.toEntity(sampleAddressDto)).thenReturn(sampleAddress);
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);
        when(addressMapper.toDto(sampleAddress)).thenReturn(sampleAddressDto);

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        AddressDto response = addressService.createAddress(customerId, sampleAddressDto, session);

        assertEquals("Cairo", response.getCity()); // Ensure the city matches the DTO
        verify(addressRepository).save(any(Address.class)); // Ensure the address was saved
    }

    @Test
    void createAddress_shouldThrow_whenInvalidFields() {
        sampleAddressDto.setCountry(null); // Invalid country

        when(session.getAttribute("userId")).thenReturn(customerId);

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        assertThrows(ResponseStatusException.class, () -> addressService.createAddress(customerId, sampleAddressDto, session));
    }

    @Test
    void createAddress_shouldThrow_whenCustomerNotFound() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        assertThrows(ResponseStatusException.class, () -> addressService.createAddress(customerId, sampleAddressDto, session));
    }

    // ----------- UPDATE -----------

    @Test
    void updateAddress_shouldUpdate_whenValid() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(1L, customerId)).thenReturn(Optional.of(sampleAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(sampleAddress);
        when(addressMapper.toDto(sampleAddress)).thenReturn(sampleAddressDto);

        sampleAddressDto.setCity("Alexandria");

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        AddressDto response = addressService.updateAddress(customerId, 1L, sampleAddressDto, session);

        assertEquals("Alexandria", response.getCity());
    }

    @Test
    void updateAddress_shouldThrow_whenNotFound() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(1L, customerId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> addressService.updateAddress(customerId, 1L, sampleAddressDto, session));
    }

    @Test
    void updateAddress_shouldThrow_whenInvalidFields() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        sampleAddressDto.setStreetName(""); // Invalid street name

        assertThrows(ResponseStatusException.class, () -> addressService.updateAddress(customerId, 1L, sampleAddressDto, session));
    }

    // ----------- DELETE -----------

    @Test
    void deleteAddress_shouldDelete_whenExists() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(1L, customerId)).thenReturn(Optional.of(sampleAddress));

        // Mock the ownership validation
        doNothing().when(idOwnership).validateIdOwnership(customerId, session);

        addressService.deleteAddress(customerId, 1L, session);

        verify(addressRepository).delete(any(Address.class));
    }

    @Test
    void deleteAddress_shouldThrow_whenNotFound() {
        when(session.getAttribute("userId")).thenReturn(customerId);
        when(addressRepository.findByIdAndCustomerId(1L, customerId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> addressService.deleteAddress(customerId, 1L, session));
    }

    @Test
    void shouldThrowForbidden_whenUserNotOwner() {
        // Simulate a different logged-in user
        Long loggedInUserId = 999L; // A user different from customerId

        when(session.getAttribute("userId")).thenReturn(loggedInUserId); // Simulating different logged-in user
        when(addressRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList()); // Mock empty list

        // Ensure that validateIdOwnership is invoked and throws ResponseStatusException
        doThrow(new ResponseStatusException(FORBIDDEN, "You can only modify your own addresses."))
                .when(idOwnership).validateIdOwnership(customerId, session);

        // Check that the exception is thrown
        assertThrows(ResponseStatusException.class,
                () -> addressService.getAddressesForCustomer(customerId, session));
    }


}
