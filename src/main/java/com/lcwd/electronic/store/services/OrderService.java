package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.CreateOrderRequest;
import com.lcwd.electronic.store.dtos.OrderDto;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.entities.Order;

import java.util.List;

public interface OrderService {
    // CREATE ORDER
    OrderDto createOrder(CreateOrderRequest request);//(OrderDto orderDto, int userId, int cartId);

    // REMOVE ORDER
    void removeOrder(int orderId);

    // GET ORDERS OF USER
    List<OrderDto> getOrdersOfUser(int userId);

    // GET ORDERS
    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDirection);

    // UPDATE ORDER
    OrderDto updateOrder(OrderDto orderDto, int orderId);
    // ORDER METHOD(LOGIC) RELATED TO ORDER
}
