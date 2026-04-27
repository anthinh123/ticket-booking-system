package com.thinh.inventory_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationResponse {
    Long reservationId;
    Long seatId;
    String status;
    LocalDateTime expiresAt;
}
