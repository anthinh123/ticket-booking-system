package com.thinh.payment_service.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String paymentMethod;
}
