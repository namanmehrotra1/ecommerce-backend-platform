package com.lcwd.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcwd.electronic.store.entities.CartItems;
import com.lcwd.electronic.store.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CartDto {
    private int cartId;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;

    private UserDto user;
    private List<CartItemsDto> cartItems;
}
