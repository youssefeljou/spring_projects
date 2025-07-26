package com.example.ecommerce.service.address;

import com.example.ecommerce.dto.AddressDto;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.util.IdOwnership;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final IdOwnership idOwnership;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, CustomerRepository customerRepository, IdOwnership idOwnership, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.idOwnership = idOwnership;
        this.addressMapper = addressMapper;
    }

    // Get all addresses for a customer
    public List<AddressDto> getAddressesForCustomer(Long customerId, HttpSession session) {
        // Validate that the customerId matches the logged-in user's ID
        idOwnership.validateIdOwnership(customerId, session);

        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        return addresses.stream()
                .map(addressMapper::toDto)
                .toList(); // Convert List of Address entities to List of AddressDto
    }

    // Get a specific address for a customer
    public AddressDto getAddress(Long customerId, Long addressId, HttpSession session) {
        // Validate that the customerId matches the logged-in user's ID
        idOwnership.validateIdOwnership(customerId, session);

        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found for this customer"));
        return addressMapper.toDto(address);
    }

    // Create a new address for a customer
    public AddressDto createAddress(Long customerId, AddressDto addressDto, HttpSession session) {
        // Validate that the customerId matches the logged-in user's ID
        idOwnership.validateIdOwnership(customerId, session);

        // Validate the provided address fields
        validateAddressFields(addressDto);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        // Convert DTO to entity and set customer entity
        Address address = addressMapper.toEntity(addressDto);
        address.setCustomer(customer);

        Address createdAddress = addressRepository.save(address);
        return addressMapper.toDto(createdAddress);
    }

    // Update a specific address for a customer
    public AddressDto updateAddress(Long customerId, Long addressId, AddressDto addressDto, HttpSession session) {
        // Validate that the customerId matches the logged-in user's ID
        idOwnership.validateIdOwnership(customerId, session);

        // Validate the provided address fields
        validateAddressFields(addressDto);

        // Fetch the existing address
        Address existingAddress = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found for this customer"));

        // Update the address fields
        existingAddress.setCountry(addressDto.getCountry());
        existingAddress.setCity(addressDto.getCity());
        existingAddress.setStreetName(addressDto.getStreetName());
        existingAddress.setHouseNumber(addressDto.getHouseNumber());
        existingAddress.setApartmentNumber(addressDto.getApartmentNumber());
        existingAddress.setPostCode(addressDto.getPostCode());

        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toDto(updatedAddress);
    }

    // Delete a specific address for a customer
    public void deleteAddress(Long customerId, Long addressId, HttpSession session) {
        // Validate that the customerId matches the logged-in user's ID
        idOwnership.validateIdOwnership(customerId, session);

        // Fetch the address to delete
        Address address = addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found for this customer"));

        // Delete the address
        addressRepository.delete(address);
    }

    // Validate address fields (can expand this as needed)
    private void validateAddressFields(AddressDto addressDto) {
        if (addressDto.getCountry() == null || addressDto.getCountry().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Country is required.");
        }
        if (addressDto.getCity() == null || addressDto.getCity().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City is required.");
        }
        if (addressDto.getStreetName() == null || addressDto.getStreetName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Street name is required.");
        }
        if (addressDto.getHouseNumber() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "House number is required and must be positive.");
        }
        if (addressDto.getApartmentNumber() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apartment number must be zero or greater.");
        }
        if (addressDto.getPostCode() == null || addressDto.getPostCode().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post code is required.");
        }
    }
}
