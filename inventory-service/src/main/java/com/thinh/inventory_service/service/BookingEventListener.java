package com.thinh.inventory_service.service;

import com.thinh.inventory_service.dto.event.BookingEvent;
import com.thinh.inventory_service.entity.SeatStatus;
import com.thinh.inventory_service.repository.ReservationRepository;
import com.thinh.inventory_service.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingEventListener {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @KafkaListener(topics = "booking-success", groupId = "inventory-group")
    @Transactional
    public void handleBookingSuccess(BookingEvent event) {
        log.info("Received booking success event for booking ID: {}, seats: {}", 
                event.getBookingId(), event.getSeatIds());

        if (event.getSeatIds() == null || event.getSeatIds().isEmpty()) {
            log.warn("No seats found in booking success event for booking ID: {}", event.getBookingId());
            return;
        }

        // 1. Update Seat status to BOOKED
        int updatedSeats = seatRepository.updateStatusByIds(event.getSeatIds(), SeatStatus.BOOKED);
        log.info("Updated {} seats to BOOKED status", updatedSeats);

        // 2. Update Reservation status to COMPLETED
        int updatedReservations = reservationRepository.updateStatusBySeatIds(event.getSeatIds(), "COMPLETED");
        log.info("Updated {} reservations to COMPLETED status", updatedReservations);
    }
}
