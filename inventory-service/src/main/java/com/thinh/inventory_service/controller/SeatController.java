package com.thinh.inventory_service.controller;

import com.thinh.inventory_service.entity.Seat;
import com.thinh.inventory_service.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SeatController {
    
    private final SeatRepository seatRepository;

    @GetMapping("/events/{eventId}/seats")
    public List<Seat> getSeatsByEvent(@PathVariable Long eventId) {
        return seatRepository.findByEventId(eventId);
    }

    @GetMapping("/seats/{id}")
    public Seat getSeatById(@PathVariable Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SEAT_NOT_FOUND"));
    }
}
