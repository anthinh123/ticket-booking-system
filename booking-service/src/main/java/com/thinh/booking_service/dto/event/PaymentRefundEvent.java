package com.thinh.booking_service.dto.event;

import lombok.*;
import org.springframework.modulith.events.Externalized;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Externalized("payment-refund")
public class PaymentRefundEvent {
    private Long bookingId;
    private String paymentId;
    private String status;
    private LocalDateTime timestamp;
}
