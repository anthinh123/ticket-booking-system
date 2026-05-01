package com.thinh.booking_service.controller;

import com.thinh.booking_service.dto.request.BookingRequest;
import com.thinh.booking_service.dto.request.PayBookingRequest;
import com.thinh.booking_service.dto.response.ApiResponse;
import com.thinh.booking_service.entity.Booking;
import com.thinh.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<Booking> createBooking(
            @RequestBody BookingRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        return ApiResponse.<Booking>builder()
                .message("Booking confirmed successfully")
                .result(bookingService.createBooking(request, userId))
                .build();
    }

    @PostMapping("/{bookingId}/pay")
    public ApiResponse<Booking> payBooking(
            @PathVariable Long bookingId,
            @RequestBody PayBookingRequest request,
            @RequestHeader("X-User-Id") String userId) {
        
        return ApiResponse.<Booking>builder()
                .message("Payment initialize successful")
                .result(bookingService.payBooking(bookingId, request, userId))
                .build();
    }
}
