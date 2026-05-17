package com.thinh.booking_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.modulith.events.Externalized;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Externalized("booking-success")
public class BookingEvent {
    private Long bookingId;
    private List<Long> seatIds;
    private String status;
}
