package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    private Float total;
    private Long customerId;

    private LocalDateTime expiresAt;

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ShoppingCart{");
        sb.append("id=").append(id);
        sb.append(", customerId=").append(customerId);

        sb.append(", items=[");
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(i));
                if (i < items.size() - 1) {
                    sb.append(", ");
                }
            }
        } else {
            sb.append("no items");
        }
        sb.append("]");

        sb.append(" , total=").append(total);
        sb.append('}');
        return sb.toString();
    }
}

