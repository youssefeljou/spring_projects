package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//    Optional<Order> findTopByShoppingCartId(Long cartId);
//
//    Optional<Order> findByPaymentIntentId(String id);
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    boolean existsByShoppingCart(ShoppingCart cart);
}
