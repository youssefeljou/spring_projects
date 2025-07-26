package com.example.ecommerce.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartRequestDto {
   // private Long shoppingCartId;
    private Long productId;
    private int quantity;


//    public Long getShoppingCartId() {
//        return shoppingCartId;
//    }
//
//    public void setShoppingCartId(Long shoppingCartId) {
//        this.shoppingCartId = shoppingCartId;
//    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
