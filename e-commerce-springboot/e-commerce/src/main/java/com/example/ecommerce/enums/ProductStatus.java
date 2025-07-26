package com.example.ecommerce.enums;

public enum ProductStatus {
    ACTIVE,
    DISCONTINUED, //to prevent the user from increasing the quantity at cart "we're not selling it anymore, but you will receive your booked quantity"
    DELETED //soft delete to hide it from the system but not from the database
}
