package com.thinh.booking_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SEAT_NOT_FOUND(2001, "Seat not found in inventory", HttpStatus.NOT_FOUND),
    INVALID_SEAT_FOR_EVENT(2002, "Seat does not belong to the requested event", HttpStatus.BAD_REQUEST),
    BOOKING_NOT_FOUND(2003, "Booking not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED(2004, "Payment processing failed", HttpStatus.PAYMENT_REQUIRED),
    SEAT_ALREADY_RESERVED(2005, "Seat is already reserved by another user", HttpStatus.CONFLICT),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
