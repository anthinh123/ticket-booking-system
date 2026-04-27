package com.thinh.inventory_service.exception;

public class ReservationFailureException extends AppException {
    public ReservationFailureException(ErrorCode errorCode) {
        super(errorCode);
    }
}
