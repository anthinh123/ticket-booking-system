package com.thinh.inventory_service.repository;

import com.thinh.inventory_service.entity.Seat;
import com.thinh.inventory_service.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventId(Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE Seat s SET s.status = :status, s.reservedBy = :userId, s.reservedUntil = :expiresAt " +
           "WHERE s.id = :seatId AND s.status = com.thinh.inventory_service.entity.SeatStatus.AVAILABLE")
    int reserveSeatIfAvailable(@Param("seatId") Long seatId, 
                               @Param("status") SeatStatus status, 
                               @Param("userId") String userId, 
                               @Param("expiresAt") LocalDateTime expiresAt);
}
