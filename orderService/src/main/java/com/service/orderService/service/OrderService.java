package com.service.orderService.service;

import com.service.orderService.entities.Order;
import com.service.orderService.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order);
    Order getOrderById(String id);
    List<Order> getAllOrders();
    void updateOrderStatus(String id, OrderStatus status);
    void deleteOrder(String id);
}
