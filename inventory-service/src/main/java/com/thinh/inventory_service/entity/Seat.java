package com.thinh.inventory_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    private String section;
    
    @Column(name = "row_number")
    private String rowNumber;

    @Column(name = "seat_type")
    private String seatType;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String status;

    @Version
    private Long version;

    @Column(name = "reserved_by")
    private String reservedBy;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
