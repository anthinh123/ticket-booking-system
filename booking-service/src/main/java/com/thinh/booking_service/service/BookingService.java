package com.thinh.booking_service.service;

import com.thinh.booking_service.dto.external.SeatResponse;
import com.thinh.booking_service.dto.request.BookingRequest;
import com.thinh.booking_service.entity.Booking;
import com.thinh.booking_service.entity.BookingSeat;
import com.thinh.booking_service.exception.AppException;
import com.thinh.booking_service.exception.ErrorCode;
import com.thinh.booking_service.repository.BookingRepository;
import com.thinh.booking_service.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final RestTemplate restTemplate;

    private final String INVENTORY_URL = "http://127.0.0.1:8083/api/v1/seats/";

    @Transactional
    public Booking createBooking(BookingRequest request, String userId) {
        // 1. Create a Booking Reference
        String reference = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Initialize Booking
        Booking booking = Booking.builder()
                .eventId(request.getEventId())
                .userId(userId)
                .bookingReference(reference)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        booking = bookingRepository.save(booking);

        BigDecimal total = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        // 3. Process each seat
        for (Long seatId : request.getSeatIds()) {
            // Fetch seat details from Inventory Service
            SeatResponse seatInfo;
            try {
                seatInfo = restTemplate.getForObject(INVENTORY_URL + seatId, SeatResponse.class);
            } catch (Exception e) {
                throw new AppException(ErrorCode.SEAT_NOT_FOUND);
            }
            
            if (seatInfo == null) throw new AppException(ErrorCode.SEAT_NOT_FOUND);
            
            // Validate seat belongs to the event
            if (!seatInfo.getEventId().equals(request.getEventId())) {
                throw new AppException(ErrorCode.INVALID_SEAT_FOR_EVENT);
            }

            BookingSeat bs = BookingSeat.builder()
                    .bookingId(booking.getId())
                    .seatId(seatId)
                    .price(seatInfo.getPrice())
                    .build();
            
            bookingSeats.add(bs);
            total = total.add(seatInfo.getPrice());
        }

        // 4. Save seats and update total
        bookingSeatRepository.saveAll(bookingSeats);
        booking.setTotalAmount(total);

        // 5. Simulate Payment Success
        booking.setStatus("CONFIRMED");
        booking.setPaymentStatus("SUCCESS");
        booking.setPaymentId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setConfirmedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }
}
