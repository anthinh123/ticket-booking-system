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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final RedisLockService redisLockService;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    
    @Value("${app.ticketing.lock-duration}")
    private Duration lockDuration;

    @Transactional
    public List<Reservation> reservations(List<Long> seatIds, String userId) {
        List<Long> acquiredLocks = new ArrayList<>();
        try {
            List<Reservation> results = new ArrayList<>();
            for (Long seatId : seatIds) {
                results.add(reserveSingleSeat(seatId, userId, acquiredLocks));
            }
            return results;
        } catch (Exception e) {
            rollbackRedisLocks(acquiredLocks);
            throw e;
        }
    }

    private Reservation reserveSingleSeat(Long seatId, String userId, List<Long> acquiredLocks) {
        // 1. Acquire Redis Lock
        if (!redisLockService.acquireSeatLock(seatId, userId)) {
            throw new ReservationFailureException(ErrorCode.LOCK_ACQUISITION_FAILED);
        }
        acquiredLocks.add(seatId);

        // 2. Fetch Seat
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ReservationFailureException(ErrorCode.SEAT_NOT_FOUND));

        // 3. Update Status
        LocalDateTime expiresAt = LocalDateTime.now().plus(lockDuration);
        int updatedRows = seatRepository.reserveSeatIfAvailable(seatId, SeatStatus.RESERVED, userId, expiresAt);

        if (updatedRows == 0) {
            throw new ReservationFailureException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        // 4. Save Reservation
        Reservation reservation = Reservation.builder()
                .seatId(seatId)
                .eventId(seat.getEventId())
                .userId(userId)
                .expiresAt(expiresAt)
                .status("ACTIVE")
                .price(seat.getPrice())
                .build();

        return reservationRepository.save(reservation);
    }

    private void rollbackRedisLocks(List<Long> acquiredLocks) {
        for (Long lockedId : acquiredLocks) {
            redisLockService.releaseSeatLock(lockedId);
        }
    }

    @Transactional
    public Reservation reservation(Long seatId, String userId) {
        return reservations(List.of(seatId), userId).get(0);
    }

}
