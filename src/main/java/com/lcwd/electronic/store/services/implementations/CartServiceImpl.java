package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.AddItemsToCartRequest;
import com.lcwd.electronic.store.dtos.CartDto;
import com.lcwd.electronic.store.entities.Cart;
import com.lcwd.electronic.store.entities.CartItems;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.BadRequestException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.CartItemsRepository;
import com.lcwd.electronic.store.repositories.CartRepository;
import com.lcwd.electronic.store.repositories.ProductRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.CartService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Override
    public CartDto addItemsToCart(int userId, AddItemsToCartRequest request) {
        //Fetch the User form userId
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID ; " + userId + " not found !!", HttpStatus.NOT_FOUND));
        int quantity = request.getQuantity();
        int productId = request.getProductId();
        if (quantity <= 0) {
            throw new BadRequestException("Requested Quantity is not valid !!", HttpStatus.BAD_REQUEST);
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with ID : " + productId, HttpStatus.NOT_FOUND));
//        Cart cart;
//        try {
//            cart = cartRepository.findByUser(user).get();
//        } catch (NoSuchElementException ex) {
//            cart = new Cart();
//            cart.setCreatedAt(new Date());
//            cart.setCartItems(new ArrayList<>());
//        }

        // BELOW IS THE MORE OPTIMISED VERSION
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCreatedAt(new Date());
            newCart.setCartItems(new ArrayList<>());
            return newCart;
        });

        // Perform Cart Operation
        // If cart items are already present
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItems> items = Optional.ofNullable(cart.getCartItems()).orElseGet(ArrayList::new);

//        items = items.stream().map(item -> {
//            if (item.getProduct().getProductID() == productId) {
//                // item is already present in the cart
//                item.setQuantity(quantity);
//                item.setTotalPrice(quantity * product.getDiscountedPrice());
//                updated.set(true);
//            }
//            return item;
//        }).collect(Collectors.toList());

        // BELOW IS THE MORE OPTIMISED VERSION

        items.forEach(item -> {
            if (item.getProduct().getProductID() == productId) {
                // item is already present in the cart
                item.setQuantity(quantity);
                item.setTotalPrice(quantity * product.getDiscountedPrice());
                updated.set(true);
            }
        });
//        cart.setCartItems(updatedItems);
        // Create Items
        if (!updated.get()) {
            CartItems cartItems = CartItems.builder()
                    .quantity(quantity)
                    .product(product)
                    .cart(cart)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .build();
            cart.getCartItems().add(cartItems);
        }
        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);
        logger.info("UPDATED CART : {} ", updatedCart);
        return modelMapper.map(updatedCart, CartDto.class);
    }

    @Override
    public void removeItemsFromCart(int userId, int cartItem) {
        //Conditions

        CartItems cartItems = cartItemsRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFoundException("No Cart Item Found with ID : " + cartItem, HttpStatus.NOT_FOUND));
        if(cartItems.getCart().getUser().getId()!=userId){
            throw new BadRequestException("Cart item does not belong to this user", HttpStatus.BAD_REQUEST);
        }
        logger.info("DELETED CART ITEM {} ", cartItems);
        cartItemsRepository.delete(cartItems);
    }

    @Override
    public void clearCart(int userId) {
        // Fetch the user from DB
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID ; " + userId + " not found !!", HttpStatus.NOT_FOUND));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found !!", HttpStatus.NOT_FOUND));
        logger.info("DELETED CART : {} ", cart);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID ; " + userId + " not found !!", HttpStatus.NOT_FOUND));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found !!", HttpStatus.NOT_FOUND));
        return modelMapper.map(cart, CartDto.class);
    }
}
