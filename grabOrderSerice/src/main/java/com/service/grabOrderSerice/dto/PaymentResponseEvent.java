package com.service.grabOrderSerice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseEvent {
    private String orderId;
    private boolean success;
    private String reason;
}
