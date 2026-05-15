package com.lcwd.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "JPA_CART_ITEMS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cartItemId;
    // Mapping with Product, only require Products in Cart, therefore unidirectional is only required.
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
    private int quantity;
    private double totalPrice;

    // Mapping with Cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID")
    private Cart cart;

}
