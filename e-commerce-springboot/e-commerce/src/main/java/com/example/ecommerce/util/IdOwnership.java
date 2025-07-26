package com.example.ecommerce.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class IdOwnership {
    // Validate that the address belongs to the logged-in customer
    public void validateIdOwnership(Long id, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("userId");
        if (!id.equals(loggedInUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own addresses.");
        }
    }
}
