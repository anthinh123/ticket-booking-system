package com.thinh.booking_service.dto.external;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long reservationId;
    private Long seatId;
    private String status;
    private LocalDateTime expiresAt;
    private BigDecimal price;
}
