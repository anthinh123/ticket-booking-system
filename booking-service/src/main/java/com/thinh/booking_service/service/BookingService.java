package com.thinh.booking_service.service;

import com.thinh.booking_service.dto.external.ReservationRequest;
import com.thinh.booking_service.dto.external.ReservationResponse;
import com.thinh.booking_service.dto.request.BookingRequest;
import com.thinh.booking_service.dto.response.ApiResponse;
import com.thinh.booking_service.entity.Booking;
import com.thinh.booking_service.entity.BookingSeat;
import com.thinh.booking_service.exception.AppException;
import com.thinh.booking_service.exception.ErrorCode;
import com.thinh.booking_service.repository.BookingRepository;
import com.thinh.booking_service.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final RestTemplate restTemplate;

    private final String RESERVATION_URL = "http://127.0.0.1:8083/api/v1/reservations";

    @Transactional
    public Booking createBooking(BookingRequest request, String userId) {
        // 1. Request to lock seats in Inventory Service
        List<ReservationResponse> reservations = reserveSeats(request.getSeatIds(), userId);

        // 2. Initialize Booking
        Booking booking = initializeBooking(request.getEventId(), userId);

        // 3. Process Booking Seats and calculate total
        processBookingSeats(booking, reservations);

        return bookingRepository.save(booking);
    }

    private List<ReservationResponse> reserveSeats(List<Long> seatIds, String userId) {
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .seatIds(seatIds)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId);
        HttpEntity<ReservationRequest> entity = new HttpEntity<>(reservationRequest, headers);

        try {
            ApiResponse<List<ReservationResponse>> response =
                    restTemplate.exchange(
                            RESERVATION_URL,
                            HttpMethod.POST,
                            entity,
                            new ParameterizedTypeReference<ApiResponse<List<ReservationResponse>>>() {
                            }
                    ).getBody();

            if (response == null || response.getResult() == null) {
                throw new AppException(ErrorCode.SEAT_ALREADY_RESERVED);
            }
            return response.getResult();
        } catch (Exception e) {
            throw new AppException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    private Booking initializeBooking(Long eventId, String userId) {
        String reference = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = Booking.builder()
                .eventId(eventId)
                .userId(userId)
                .bookingReference(reference)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        return bookingRepository.save(booking);
    }

    private void processBookingSeats(Booking booking, List<ReservationResponse> reservations) {
        BigDecimal total = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        for (ReservationResponse res : reservations) {
            BookingSeat bs = BookingSeat.builder()
                    .bookingId(booking.getId())
                    .seatId(res.getSeatId())
                    .price(res.getPrice())
                    .build();

            bookingSeats.add(bs);
            total = total.add(res.getPrice());
        }

        bookingSeatRepository.saveAll(bookingSeats);
        booking.setTotalAmount(total);
    }


}
