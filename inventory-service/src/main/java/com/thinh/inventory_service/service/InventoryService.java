package com.thinh.inventory_service.service;

import com.thinh.inventory_service.entity.Reservation;
import com.thinh.inventory_service.entity.Seat;
import com.thinh.inventory_service.entity.SeatStatus;
import com.thinh.inventory_service.exception.ErrorCode;
import com.thinh.inventory_service.exception.ReservationFailureException;
import com.thinh.inventory_service.repository.ReservationRepository;
import com.thinh.inventory_service.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final RedisLockService redisLockService;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    
    @Value("${app.ticketing.lock-duration}")
    private Duration lockDuration;

    @Transactional
    public void reservation(Long seatId, String userId) {
        // 1. Acquire Redis Lock (Duration matches the business TTL)
        boolean isLockAcquired = redisLockService.acquireSeatLock(seatId, userId);
        
        if (!isLockAcquired) {
            throw new ReservationFailureException(ErrorCode.LOCK_ACQUISITION_FAILED);
        }

        try {
            // Find the seat to get the eventId (needed for Reservation record)
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ReservationFailureException(ErrorCode.SEAT_NOT_FOUND));

            LocalDateTime expiresAt = LocalDateTime.now().plus(lockDuration);

            // 2. Perform Atomic Update on Seat Status
            int updatedRows = seatRepository.reserveSeatIfAvailable(seatId, SeatStatus.RESERVED, userId, expiresAt);

            if (updatedRows == 0) {
                // If update failed, another user beat us or seat is not AVAILABLE
                redisLockService.releaseSeatLock(seatId);
                throw new ReservationFailureException(ErrorCode.SEAT_ALREADY_RESERVED);
            }

            // 3. Create a record in the Reservations table
            Reservation reservation = Reservation.builder()
                    .seatId(seatId)
                    .eventId(seat.getEventId())
                    .userId(userId)
                    .expiresAt(expiresAt)
                    .status("ACTIVE")
                    .build();
            
            reservationRepository.save(reservation);
            
            // NOTE: We DO NOT release the Redis lock here. 
            // It will stay in Redis for 10 minutes (or until payment is successful).

        } catch (Exception e) {
            // 4. On ANY error, we must release the Redis lock so others can try
            redisLockService.releaseSeatLock(seatId);
            
            if (e instanceof ReservationFailureException) {
                throw e;
            }
            throw new ReservationFailureException(ErrorCode.RESERVATION_FAILED);
        }
    }
}
