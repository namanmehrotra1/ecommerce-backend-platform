package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.CreateOrderRequest;
import com.lcwd.electronic.store.dtos.OrderDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.*;
import com.lcwd.electronic.store.exceptions.BadRequestException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.CartRepository;
import com.lcwd.electronic.store.repositories.OrderRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public OrderDto createOrder(CreateOrderRequest request) {//(OrderDto orderDto, int userId, int cartId) {
        // FETCH USER
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User with ID : " + request.getUserId() + " is not found", HttpStatus.NOT_FOUND));

        // FETCH CART
        Cart cart = cartRepository.findById(request.getCartId()).orElseThrow(() -> new ResourceNotFoundException("Cart with ID : " + request.getCartId() + " is not found", HttpStatus.NOT_FOUND));
        List<CartItems> cartItems = cart.getCartItems();
        if (cartItems.size() <= 0) {
            throw new BadRequestException("Invalid number of items in cart !!", HttpStatus.BAD_REQUEST);
        }
        // OTHER CHECKS

        Order order = Order.builder()
                .billingName(request.getBillingName())
                .billingAddress(request.getBillingAddress())
                .orderedDate(request.getOrderedDate())
                .deliveredDate(request.getDeliveredDate())
                .billingPhone(request.getBillingPhone())
                .orderAmount(request.getOrderAmount())
                .paymentStatus(request.getPaymentStatus())
                .orderStatus(request.getOrderStatus())
                .user(user)
                .build(); //orderItems, orderAmount
        AtomicReference<Double> orderAmount = new AtomicReference<>(0.0);
        List<OrderItems> orderItems = cartItems.stream().map(cartItem -> {
            // Cart items to order items
            OrderItems orderItem = OrderItems.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();
            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public void removeOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with ID : " + orderId + " is not found", HttpStatus.NOT_FOUND));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrdersOfUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID : " + userId + " is not found", HttpStatus.NOT_FOUND));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(order -> modelMapper.map(order, OrderDto.class)).collect(Collectors.toList());
    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);
        return Helper.getPageableResponse(page, OrderDto.class);
    }

    @Override
    public OrderDto updateOrder(OrderDto orderDto, int orderId) {
        Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with ID : " + orderId, HttpStatus.NOT_FOUND));
//        AtomicReference<Double> recalculatedAmount = new AtomicReference<>(0.0);
//        existingOrder.getOrderItems().forEach(item ->
//                recalculatedAmount.updateAndGet(amount -> amount + item.getTotalPrice()));
        if (Optional.ofNullable(orderDto.getOrderStatus()).map(String::isEmpty).orElse(true) ||
                (Optional.ofNullable(orderDto.getPaymentStatus()).map(String::isEmpty).orElse(true) ||
                        Optional.ofNullable(orderDto.getBillingAddress()).map(String::isEmpty).orElse(true) ||
                        Optional.ofNullable(orderDto.getBillingName()).map(String::isEmpty).orElse(true) ||
                        Optional.ofNullable(orderDto.getBillingPhone()).map(String::isEmpty).orElse(true) ||
                        Optional.ofNullable(orderDto.getDeliveredDate()).isEmpty()
                )) {
            throw new BadRequestException("Some required fields are EMPTY !! , please check !!", HttpStatus.BAD_REQUEST);
        }
        Order updatedOrder = Order.builder()
                .orderId(existingOrder.getOrderId())
                .user(existingOrder.getUser())
                .orderStatus(orderDto.getOrderStatus())
                .paymentStatus(orderDto.getPaymentStatus())
                .orderAmount(existingOrder.getOrderAmount())
                .orderedDate(existingOrder.getOrderedDate())
                .billingPhone(orderDto.getBillingPhone())
                .deliveredDate(orderDto.getDeliveredDate())
                .billingAddress(orderDto.getBillingAddress())
                .billingName(orderDto.getBillingName())
                .orderItems(existingOrder.getOrderItems())
                .build();
        Order savedOrder = orderRepository.save(updatedOrder);
        return modelMapper.map(savedOrder, OrderDto.class);
    }
}
