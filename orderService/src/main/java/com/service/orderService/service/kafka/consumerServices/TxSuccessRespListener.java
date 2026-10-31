package com.service.orderService.service.kafka.consumerServices;

import com.service.orderService.dto.NotificationDto;
import com.service.orderService.dto.PaymentResponseEvent;
import com.service.orderService.dto.UserResponseDto;
import com.service.orderService.entities.Order;
import com.service.orderService.enums.OrderStatus;
import com.service.orderService.repo.OrderRepository;
import com.service.orderService.service.OrderService;
import com.service.orderService.service.feignClients.UserClient;
import com.service.orderService.service.kafka.producerServices.SendNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TxSuccessRespListener {

    private final OrderService orderService;
    private final OrderRepository repository;
    private final SendNotificationService notificationService;
    private final UserClient userClient;

    @KafkaListener(topics = "transaction.response.event", groupId = "transaction.response.event.group")
    public void orderSuccessfullyPlaced(PaymentResponseEvent dto) throws Exception {
        Optional<Order> optionalOrder = repository.findById(dto.getOrderId());

        if (optionalOrder.isEmpty()) {
            // Optional logging if order not found
            System.err.println("Order not found for ID: " + dto.getOrderId());
            return;
        }

        Order order = optionalOrder.get();
        order.setToken(UUID.randomUUID().toString());
        repository.save(order);

        orderService.updateOrderStatus(order.getId(), OrderStatus.COMPLETED);

        UserResponseDto user = userClient.getUserByIdHandler(order.getUserId());

        String msgToSend = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <style>
                    body {
                      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                      background: #f9fafc;
                      color: #333;
                      padding: 20px;
                    }
                    .card {
                      max-width: 500px;
                      margin: auto;
                      background: #fff;
                      border-radius: 12px;
                      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                      padding: 24px;
                      text-align: center;
                    }
                    h2 {
                      color: #2a9d8f;
                      margin-bottom: 12px;
                    }
                    .token-box {
                      background: #f1f5f9;
                      padding: 12px;
                      border-radius: 8px;
                      font-size: 18px;
                      font-weight: bold;
                      margin: 12px 0;
                      display: flex;
                      justify-content: space-between;
                      align-items: center;
                    }
                    button {
                      background: #2a9d8f;
                      color: #fff;
                      border: none;
                      padding: 8px 14px;
                      border-radius: 6px;
                      cursor: pointer;
                      font-size: 14px;
                    }
                    button:hover {
                      background: #21867a;
                    }
                    .details {
                      margin: 16px 0;
                      font-size: 15px;
                      line-height: 1.6;
                    }
                    .status {
                      margin-top: 16px;
                      font-weight: bold;
                      color: #264653;
                    }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <h2>🎉 Order Placed Successfully!</h2>
                    <p class="details">
                      Thank you for your order. The payment of <strong>$%s</strong> 
                      has been deducted successfully.
                    </p>
                    <div class="token-box">
                      <span>Token: %s</span>
                      <button onclick="navigator.clipboard.writeText('%s')">Copy</button>
                    </div>
                    <p class="details">
                      Quantity: <strong>%s</strong>
                    </p>
                    <p class="status">
                      Current Status: %s
                    </p>
                    <p class="details">
                      ✅ If the status is <strong>Complete</strong>, once you grab your order it will update to <strong>Collected</strong>.
                    </p>
                  </div>
                </body>
                </html>
                """,
                order.getTotalPrice(),
                order.getToken(),
                order.getToken(),
                order.getQuantity(),
                order.getStatus()
        );

        notificationService.sendNotificationEvent(
                new NotificationDto(
                        user.getEmail(),
                        msgToSend,
                        "ORDER AND TRANSACTION",
                        "SUCCESS",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }
}
