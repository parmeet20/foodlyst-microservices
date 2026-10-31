package com.service.orderService.service.kafka.consumerServices;

import com.service.orderService.dto.PaymentResponseEvent;
import com.service.orderService.enums.OrderStatus;
import com.service.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentResponseEventService {
    private final OrderService orderService;

    @KafkaListener(topics = "payment.response.event",groupId = "payment.response.event.group")
    public void calcelOrder(PaymentResponseEvent dto) {
        if (Objects.equals(dto.getReason(), "GRABBED SUCCESSFULLY")) {
            orderService.updateOrderStatus(dto.getOrderId(), OrderStatus.COLLECTED);
        }else if(dto.isSuccess()){
            orderService.updateOrderStatus(dto.getOrderId(), OrderStatus.COMPLETED);
        } else {
            orderService.updateOrderStatus(dto.getOrderId(), OrderStatus.CANCELLED);
        }
    }
}
