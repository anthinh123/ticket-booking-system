package com.thinh.booking_service.dto.external;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    private Long id;
    private Long eventId;
    private String seatNumber;
    private BigDecimal price;
    private String status;
}
