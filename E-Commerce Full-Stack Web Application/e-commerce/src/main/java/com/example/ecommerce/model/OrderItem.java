package com.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Parent-side reference; ignored during JSON serialisation to break the loop */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Order order;

    //-------- snapshot fields --------
    private Long       productId;
    private String     productName;
    private BigDecimal unitPrice;
    private int        quantity;
}
