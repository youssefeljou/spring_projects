package com.example.ecommerce.dto;

import com.example.ecommerce.enums.Status;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.ShoppingCart;
import com.example.ecommerce.validation.annotation.ValidEmail;
import com.example.ecommerce.validation.annotation.ValidId;
import com.example.ecommerce.validation.annotation.ValidPassword;
import com.example.ecommerce.validation.annotation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CustomerDto{


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    @ValidId
    private Long id;
    @NotNull
    @NotBlank
    @ValidUsername
    private String username;

    @NotNull
    @NotBlank
    @ValidEmail
    private String email;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @ValidPassword
    private String password;
    @NotNull
    //@NotBlank
    private List<String> phone;
    private List<CreditCard> creditCards;
    private List<Address> addresses;
    private Status accountStatus;
    private ShoppingCart shoppingCart;
    private List<Order> orderList;


    public CustomerDto(String username, String password, String email) {

        this.username = username;
        this.password = password;
        this.email = email;
    }

    public CustomerDto() {
    }

    public  List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
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
