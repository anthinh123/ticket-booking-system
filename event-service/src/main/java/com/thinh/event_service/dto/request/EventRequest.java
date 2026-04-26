package com.thinh.event_service.dto.request;

import com.thinh.event_service.entity.Event.EventStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    private String name;
    private LocalDateTime date;
    private String venueName;
    private Integer totalSeats;
    private EventStatus status;
    private LocalDateTime saleStartTime;
}
