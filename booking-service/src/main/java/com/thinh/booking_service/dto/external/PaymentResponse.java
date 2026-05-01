package com.thinh.booking_service.dto.external;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String status;
    private LocalDateTime timestamp;
}
