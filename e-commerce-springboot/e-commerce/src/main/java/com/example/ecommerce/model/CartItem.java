package com.example.ecommerce.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShoppingCart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private int quantity;
}
