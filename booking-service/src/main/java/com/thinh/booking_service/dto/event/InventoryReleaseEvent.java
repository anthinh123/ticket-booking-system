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
@Externalized("inventory-release")
public class InventoryReleaseEvent {
    private Long bookingId;
    private List<Long> seatIds;
    private String reason;
}
