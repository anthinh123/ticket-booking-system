package com.thinh.inventory_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "Seat is already reserved by another user"),
    LOCK_ACQUISITION_FAILED(HttpStatus.TOO_MANY_REQUESTS, "Could not acquire lock, please try again"),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "Seat not found"),
    RESERVATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Reservation failed, please try again");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
