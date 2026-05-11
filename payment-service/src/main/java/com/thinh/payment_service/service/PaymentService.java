package com.thinh.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinh.payment_service.dto.event.PaymentEvent;
import com.thinh.payment_service.dto.request.PaymentRequest;
import com.thinh.payment_service.dto.response.PaymentResponse;
import com.thinh.payment_service.entity.Payment;
import com.thinh.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Check if already processed
        Optional<Payment> existingPayment = paymentRepository.findByBookingId(request.getBookingId());
        if (existingPayment.isPresent()) {
            log.info("Duplicate request detected for booking ID: {}. Returning existing payment record.", request.getBookingId());
            Payment p = existingPayment.get();
            return PaymentResponse.builder()
                    .paymentId(p.getPaymentId())
                    .status(p.getStatus())
                    .timestamp(p.getCreatedAt())
                    .build();
        }

        log.info("Initiating payment for booking ID: {}, amount: {}", request.getBookingId(), request.getAmount());

        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Persist Payment Record (Idempotency check via Unique Constraint)
        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .paymentId(paymentId)
                .amount(request.getAmount())
                .status("INITIATED")
                .createdAt(LocalDateTime.now())
                .build();

        try {
            payment = paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent payment request for booking ID: {} detected. Fetching existing record.", request.getBookingId());
            return processPayment(request); // Recursive call will catch the record saved by the other thread
        }

        // Start async simulation
        CompletableFuture.runAsync(() -> simulateAndNotify(request.getBookingId(), paymentId));

        return PaymentResponse.builder()
                .paymentId(paymentId)
                .status(payment.getStatus())
                .timestamp(payment.getCreatedAt())
                .build();
    }

    private void simulateAndNotify(Long bookingId, String paymentId) {
        // Simulate processing delay (10-15 seconds)
        int delay = new Random().nextInt(10000) + 5000;
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
