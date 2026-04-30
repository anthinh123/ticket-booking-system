package com.thinh.inventory_service.controller;

import com.thinh.inventory_service.dto.request.ReservationRequest;
import com.thinh.inventory_service.dto.response.ApiResponse;
import com.thinh.inventory_service.dto.response.ReservationResponse;
import com.thinh.inventory_service.entity.Reservation;
import com.thinh.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final InventoryService inventoryService;

    @PostMapping
    public ApiResponse<List<ReservationResponse>> createReservation(
            @RequestBody ReservationRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        List<Reservation> reservations = inventoryService.reservations(request.getSeatIds(), userId);
        
        List<ReservationResponse> responses = reservations.stream()
                .map(reservation -> ReservationResponse.builder()
                        .reservationId(reservation.getId())
                        .seatId(reservation.getSeatId())
                        .status(reservation.getStatus())
                        .expiresAt(reservation.getExpiresAt())
                        .price(reservation.getPrice())
                        .build())
                .toList();
        
        return ApiResponse.<List<ReservationResponse>>builder()
                .message("Reservation successful")
                .result(responses)
                .build();
    }
}
