package com.thinh.booking_service.dto.request;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long eventId;
    private List<Long> seatIds;
}
