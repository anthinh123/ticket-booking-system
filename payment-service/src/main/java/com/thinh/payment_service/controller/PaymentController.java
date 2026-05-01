package com.thinh.payment_service.controller;

import com.thinh.payment_service.dto.request.PaymentRequest;
import com.thinh.payment_service.dto.response.ApiResponse;
import com.thinh.payment_service.dto.response.PaymentResponse;
import com.thinh.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .message("Payment processed")
                .result(paymentService.processPayment(request))
                .build();
    }
}
