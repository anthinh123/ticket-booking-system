package com.thinh.booking_service.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayBookingRequest {
    private String paymentMethod;
}
