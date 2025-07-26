package com.example.ecommerce.model;

import com.example.ecommerce.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import lombok.NoArgsConstructor;


import java.util.List;


@Entity
@NoArgsConstructor
public class Customer extends User {
    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> addresses;
    @Enumerated(EnumType.STRING)
    private Status accountStatus;
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    private List<CreditCard> creditCards;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Order> orderList;
    @OneToOne(cascade = CascadeType.ALL)
    private ShoppingCart shoppingCart;

    public Customer(Long customerId) {
        super();
    }


    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Status getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Status accountStatus) {
        this.accountStatus = accountStatus;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

}

