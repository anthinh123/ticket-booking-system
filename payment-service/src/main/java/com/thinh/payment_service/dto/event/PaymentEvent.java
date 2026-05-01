package com.thinh.payment_service.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long bookingId;
    private String paymentId;
    private String status;
    private LocalDateTime timestamp;
}
