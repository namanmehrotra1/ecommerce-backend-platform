package com.lcwd.electronic.store.dtos;

import com.lcwd.electronic.store.entities.Cart;
import com.lcwd.electronic.store.entities.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CartItemsDto {
    private int cartItemId;
    private ProductDto product;
    @NotNull(message = "Quantity is required !!")
    private int quantity;
    private double totalPrice;
}
