package com.lcwd.electronic.store.dtos;

import com.lcwd.electronic.store.entities.Order;
import com.lcwd.electronic.store.entities.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsDto {
    private int orderItemId;
    @NotNull(message = "Quantity is required !!")
    private int quantity;
    @NotNull(message = "Total Price is required !!")
    private int totalPrice;
    private ProductDto product;
}

