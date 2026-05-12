package com.thinh.booking_service.service;

import com.thinh.booking_service.constant.BookingStatus;
import com.thinh.booking_service.constant.PaymentStatus;
import com.thinh.booking_service.dto.event.BookingEvent;
import com.thinh.booking_service.dto.external.PaymentEvent;
import com.thinh.booking_service.entity.Booking;
import com.thinh.booking_service.entity.BookingSeat;
import com.thinh.booking_service.repository.BookingRepository;
import com.thinh.booking_service.repository.BookingSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

        if (BookingStatus.PENDING != booking.getStatus()) {
            log.warn("Booking {} is already in status: {}", booking.getId(), booking.getStatus());
            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setConfirmedAt(event.getTimestamp());

        bookingRepository.save(booking);
        log.info("Booking {} confirmed successfully", booking.getId());

        // Publish booking success event to notify Inventory Service
        List<Long> seatIds = bookingSeatRepository.findAllByBookingId(booking.getId())
                .stream()
                .map(BookingSeat::getSeatId)
                .collect(Collectors.toList());

        BookingEvent bookingEvent = BookingEvent.builder()
                .bookingId(booking.getId())
                .seatIds(seatIds)
                .status("BOOKING_SUCCESS")
                .build();

        log.info("Publishing booking-success event for booking ID: {}", booking.getId());
        kafkaTemplate.send("booking-success", bookingEvent);
    }
}
