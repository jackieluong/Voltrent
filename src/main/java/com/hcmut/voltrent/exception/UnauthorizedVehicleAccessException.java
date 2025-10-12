package com.hcmut.voltrent.exception;

public class UnauthorizedVehicleAccessException extends RuntimeException {
    public UnauthorizedVehicleAccessException(String message) {
        super(message);
    }
}
