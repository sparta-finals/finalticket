package com.sparta.finalticket.global.exception.alarm;

public class AlarmNotFoundException extends RuntimeException {
    public AlarmNotFoundException(String message) {
        super(message);
    }
}
