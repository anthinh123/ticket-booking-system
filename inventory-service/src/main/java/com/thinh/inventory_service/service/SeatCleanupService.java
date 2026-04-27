package com.thinh.inventory_service.service;

import com.thinh.inventory_service.repository.SeatRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class SeatCleanupService {

    private final SeatRepository seatRepository;

    // Runs every 10 minute to keep the seat map fresh
    @Scheduled(fixedRate = 600000)
    public void cleanup() {
        int count = seatRepository.releaseExpiredSeats(LocalDateTime.now());
        if (count > 0) {
            log.info("Successfully released {} stale reservations back to AVAILABLE", count);
        }
    }
}
