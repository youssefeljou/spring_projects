package com.example.ecommerce.controller;

import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Renders the checkout page where the customer either selects a saved card or
 * types a new one
 * prepare data for the Thymeleaf template.
 */
@Controller
@RequiredArgsConstructor
public class CheckoutPageController {

    private final ShoppingCartRepository cartRepo;
    private final CreditCardRepository   cardRepo;

    /** Stripe publishable key(test key) */
    @Value("${stripe.publishableKey}")
    private String stripePk;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/checkout/form/{cartId}")
    public String showCheckout(@PathVariable Long cartId,
                               Model model,
                               HttpServletResponse resp) {

        ShoppingCart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Long customerId = cart.getCustomerId();
        List<CreditCard> cards = cardRepo.findByCustomerId(customerId);

        model.addAttribute("cartId",  cartId);
        model.addAttribute("amount",  cart.getTotal());
        model.addAttribute("cards",   cards);
        model.addAttribute("stripePk", stripePk);

        //Prevent browser from caching card data
        resp.setHeader("Cache-Control", "no-store");

        return "checkout";
    }
}