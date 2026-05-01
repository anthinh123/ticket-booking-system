package com.thinh.booking_service.service;

import com.thinh.booking_service.dto.external.PaymentEvent;
import com.thinh.booking_service.entity.Booking;
import com.thinh.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final BookingRepository bookingRepository;

    @KafkaListener(topics = "payment-success", groupId = "booking-group")
    @Transactional
    public void handlePaymentSuccess(PaymentEvent event) {
        log.info("Received payment success event for booking ID: {}", event.getBookingId());

        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElse(null);

        if (booking == null) {
            log.error("Booking not found for ID: {}", event.getBookingId());
            return;
        }

        if (!"PENDING".equals(booking.getStatus())) {
            log.warn("Booking {} is already in status: {}", booking.getId(), booking.getStatus());
            return;
        }

        booking.setStatus("CONFIRMED");
        booking.setPaymentStatus("SUCCESS");
        booking.setConfirmedAt(event.getTimestamp());

        bookingRepository.save(booking);
        log.info("Booking {} confirmed successfully", booking.getId());
    }
}
