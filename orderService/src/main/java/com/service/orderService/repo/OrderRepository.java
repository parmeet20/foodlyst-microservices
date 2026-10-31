package com.service.orderService.repo;

import com.service.orderService.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Order findByToken(String token);
}