package com.example.ecommerce.model;

import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Setter @Getter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal amount;

    private String currency;

    private String paymentIntentId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDate orderDate;
    private LocalDate deliveryDate;

    private LocalDateTime createdAt;


    @PrePersist
    public void setDefaultDates() {
        this.orderDate = LocalDate.now();
        this.deliveryDate = orderDate.plusDays(3);
        this.createdAt = LocalDateTime.now();
    }

}