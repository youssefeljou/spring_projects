package com.example.ecommerce.model;

import com.example.ecommerce.converter.CryptoConverter;
import com.example.ecommerce.util.CardUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;


@Entity
@Getter @Setter
@Table(name = "credit_cards")
public class CreditCard {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Convert(converter = CryptoConverter.class)
    @Column(nullable = false, length = 800)
    private String cardNumberEnc;

    @Column(length=4, nullable=false)
    private String last4;

    @Column(length=12, nullable=false)
    private String brand;

    private String cardHolderName;

    @Column(nullable=false)
    private YearMonth expiration;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    public void setPlainCardNumber(String pan) {
        this.cardNumberEnc = pan;                     //CryptoConverter encrypts
        this.last4 = pan.substring(pan.length()-4);
        this.brand = CardUtil.brand(pan);
    }
    //decrypt
    public String getPlainCardNumber() {
        return cardNumberEnc;
    }

    public boolean isExpired() {
        return YearMonth.now().isAfter(expiration);
    }

}
