package com.shenq.courtbooking.common.exception;

public class CourtNotFoundException
        extends RuntimeException {

    public CourtNotFoundException(String message) {
        super(message);
    }
}