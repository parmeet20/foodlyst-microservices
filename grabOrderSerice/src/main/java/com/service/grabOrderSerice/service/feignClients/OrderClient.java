package com.service.grabOrderSerice.service.feignClients;

import com.service.grabOrderSerice.dto.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "orderService")
public interface OrderClient {
    @GetMapping("/api/v1/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id);
}
