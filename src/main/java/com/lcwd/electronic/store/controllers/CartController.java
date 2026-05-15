package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.AddItemsToCartRequest;
import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.dtos.CartDto;
import com.lcwd.electronic.store.services.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@Tag(name = "CartController", description = "APIs to check Cart Related Operations")
public class CartController {

    @Autowired
    private CartService cartService;

    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_ADMIN + "','" + AppConstants.ROLE_NORMAL + "')")
    @PostMapping("/create/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody AddItemsToCartRequest request, @PathVariable int userId) {
        CartDto addItemsToCart = cartService.addItemsToCart(userId, request);
        return new ResponseEntity<>(addItemsToCart, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_ADMIN + "','" + AppConstants.ROLE_NORMAL + "')")
    @DeleteMapping("/remove/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseMessage> removeItemsFromCart(@PathVariable int userId, @PathVariable("cartItemId") int cartItem) {
        cartService.removeItemsFromCart(userId, cartItem);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .success(true)
                .message("Item with User Id : " + userId + " and Cart Id : " + cartItem + " is removed successfully !!")
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_ADMIN + "','" + AppConstants.ROLE_NORMAL + "')")
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable int userId) {
        cartService.clearCart(userId);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .status(HttpStatus.OK)
                .message("CART IS CLEARED SUCCESSFULLY !!")
                .success(true)
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('" + AppConstants.ROLE_ADMIN + "','" + AppConstants.ROLE_NORMAL + "')")
    @GetMapping("/cartItems/{userId}")
    public ResponseEntity<CartDto> getCartItems(@PathVariable int userId) {
        CartDto cartByUser = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartByUser, HttpStatus.OK);
    }
}
