package com.example.ecommerce.controller;

import com.example.ecommerce.dto.AddressDto;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.service.address.AddressService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    // Get all addresses for a customer

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public List<AddressDto> getAddressesForCustomer(@PathVariable Long customerId, HttpSession session) {
        try {
            return addressService.getAddressesForCustomer(customerId, session);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getReason());
        }
    }

    // Get a specific address for a customer

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{addressId}")
    public AddressDto getAddress(@PathVariable Long customerId, @PathVariable Long addressId, HttpSession session) {
        try {
            return addressService.getAddress(customerId, addressId, session);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getReason());
        }
    }

    // Create a new address for a customer

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public AddressDto createAddress(@PathVariable Long customerId, @RequestBody AddressDto addressDto, HttpSession session) {
        try {
            return addressService.createAddress(customerId, addressDto, session);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getReason());
        }
    }

    // Update a specific address for a customer

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{addressId}")
    public AddressDto updateAddress(@PathVariable Long customerId, @PathVariable Long addressId, @RequestBody AddressDto addressDto, HttpSession session) {
        try {
            return addressService.updateAddress(customerId, addressId, addressDto, session);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getReason());
        }
    }

    // Delete a specific address for a customer

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long customerId, @PathVariable Long addressId, HttpSession session) {
        try {
            addressService.deleteAddress(customerId, addressId, session);
        } catch (ResponseStatusException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getReason());
        }
    }
}
