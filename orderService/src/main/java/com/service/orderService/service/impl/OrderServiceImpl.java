package com.service.orderService.service.impl;

import com.service.orderService.dto.PaymentRequestEvent;
import com.service.orderService.entities.Order;
import com.service.orderService.enums.OrderStatus;
import com.service.orderService.repo.OrderRepository;
import com.service.orderService.service.OrderService;
import com.service.orderService.service.kafka.producerServices.PaymentRequestEventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRequestEventService eventService;

    @Override
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(java.time.LocalDateTime.now());
        Order or = orderRepository.save(order);
        eventService.paymentRequestEvent(new PaymentRequestEvent(or.getId(), or.getUserId(), or.getRestaurantId(), or.getTotalPrice(), or.getQuantity(), or.getFoodOfferRequestId()));
        return or;
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void updateOrderStatus(String id, OrderStatus status) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        existingOrder.setStatus(status);
        orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(String id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        orderRepository.delete(existingOrder);
    }
}
