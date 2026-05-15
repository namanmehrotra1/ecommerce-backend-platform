package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.AddItemsToCartRequest;
import com.lcwd.electronic.store.dtos.CartDto;

public interface CartService {
    // Add items to cart
    // Case 1: Cart for user is not available, we will create the cart and then add the items
    // Case 2: If Cart is available, then add the items to cart.

    CartDto addItemsToCart(int userId, AddItemsToCartRequest request);

    // Remove items from cart
    void removeItemsFromCart(int userId, int cartItem);

    // Remove all items from cart
    void clearCart(int userId);

    CartDto getCartByUser(int userId);
}
