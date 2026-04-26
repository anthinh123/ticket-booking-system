package com.thinh.event_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "event_id")
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String name;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(name = "sale_start_time")
    private LocalDateTime saleStartTime;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    public enum EventStatus {
        UPCOMING, ON_SALE, SOLD_OUT, CANCELLED
    }
}
