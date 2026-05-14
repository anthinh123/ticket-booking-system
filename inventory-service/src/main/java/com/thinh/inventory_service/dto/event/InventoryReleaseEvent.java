package com.thinh.inventory_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReleaseEvent {
    private Long bookingId;
    private List<Long> seatIds;
    private String reason;
}
