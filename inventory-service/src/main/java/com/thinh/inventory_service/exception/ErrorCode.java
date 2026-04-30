package com.thinh.inventory_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    SEAT_ALREADY_RESERVED(1001, "Seat is already reserved by another user", HttpStatus.CONFLICT),
    LOCK_ACQUISITION_FAILED(1002, "Could not acquire lock, please try again", HttpStatus.TOO_MANY_REQUESTS),
    SEAT_NOT_FOUND(1003, "Seat not found", HttpStatus.NOT_FOUND),
    RESERVATION_FAILED(1004, "Reservation failed, please try again", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1005, "Uncategorized error", HttpStatus.BAD_REQUEST),
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
