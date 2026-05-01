package com.thinh.payment_service.service;

import com.thinh.payment_service.dto.event.PaymentEvent;
import com.thinh.payment_service.dto.request.PaymentRequest;
import com.thinh.payment_service.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Initiating payment for booking ID: {}, amount: {}", request.getBookingId(), request.getAmount());

        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Start async simulation
        simulateAndNotify(request.getBookingId(), paymentId);

        return PaymentResponse.builder()
                .paymentId(paymentId)
                .status("INITIATED")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Async
    public void simulateAndNotify(Long bookingId, String paymentId) {
        // Simulate processing delay (5-10 seconds)
        int delay = new Random().nextInt(5000) + 5000;
        try {
            log.info("Simulating payment delay of {} ms for booking {}...", delay, bookingId);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
            return;
        }

        PaymentEvent event = PaymentEvent.builder()
                .bookingId(bookingId)
                .paymentId(paymentId)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();

        log.info("Sending payment success event for booking {}: {}", bookingId, paymentId);
        kafkaTemplate.send("payment-success", event);
    }
}
