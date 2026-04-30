package com.thinh.booking_service.dto.external;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private List<Long> seatIds;
}
