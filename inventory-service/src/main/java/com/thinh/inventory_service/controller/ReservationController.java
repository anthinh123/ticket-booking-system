package com.thinh.inventory_service.controller;

import com.thinh.inventory_service.dto.request.ReservationRequest;
import com.thinh.inventory_service.dto.response.ApiResponse;
import com.thinh.inventory_service.dto.response.ReservationResponse;
import com.thinh.inventory_service.entity.Reservation;
import com.thinh.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final InventoryService inventoryService;

    @PostMapping
    public ApiResponse<ReservationResponse> createReservation(
            @RequestBody ReservationRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        Reservation reservation = inventoryService.reservation(request.getSeatId(), userId);
        
        ReservationResponse response = ReservationResponse.builder()
                .reservationId(reservation.getId())
                .seatId(reservation.getSeatId())
                .status(reservation.getStatus())
                .expiresAt(reservation.getExpiresAt())
                .build();
        
        return ApiResponse.<ReservationResponse>builder()
                .message("Reservation successful")
                .result(response)
                .build();
    }
}
