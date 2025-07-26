package com.example.ecommerce.service.order.payment_service_strategy;

import com.example.ecommerce.dto.PaymentRequestDto;
import com.example.ecommerce.dto.PaymentResultDto;
import com.example.ecommerce.enums.PaymentType;
import com.example.ecommerce.exception.DuplicateOrderException;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Qualifier("cash")
@RequiredArgsConstructor
public class CashPaymentService implements PaymentStrategy {

    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;

    @Override
    public PaymentResultDto pay(Long cartId, PaymentRequestDto request) throws DuplicateOrderException {
        ShoppingCart cart = shoppingCartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found with ID: " + cartId));

        Customer customer = customerRepository.findById(cart.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + cart.getCustomerId()));

        Order order = new Order();
        order.setShoppingCart(cart);
        order.setPaymentType(PaymentType.CASH);
        order.setCustomer(customer);

        try {
            List<OrderItem> orderItems = cart.getItems().stream()
                    .map(ci -> {
                        OrderItem oi = new OrderItem();
                        oi.setOrder(order);
                        oi.setProductId(ci.getProduct().getId());
                        oi.setProductName(ci.getProduct().getName());
                        oi.setUnitPrice(BigDecimal.valueOf(ci.getProduct().getPrice()));
                        oi.setQuantity(ci.getQuantity());
                        return oi;
                    })
                    .toList();

            order.setItems(orderItems);

            orderRepository.save(order);
            order.setShoppingCart(null);
            orderRepository.save(order);

            shoppingCartRepository.deleteById(cartId);
            customer.setShoppingCart(null);
            customerRepository.save(customer);

        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("orders_shopping_cart_id_key")) {
                throw new DuplicateOrderException("The order has already been paid.");
            }
            throw e;
        }

        return new PaymentResultDto("DONE", "Cash payment confirmed. Order placed successfully.");
    }
}
