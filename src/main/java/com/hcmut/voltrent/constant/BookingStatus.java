package com.hcmut.voltrent.constant;


import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING_PAYMENT("PENDING_PAYMENT"),
    CONFIRMED("CONFIRMED"),
    CANCELLED("CANCELLED"),
    PENDING_CONFIRMATION("PENDING_CONFIRMATION");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public static BookingStatus fromValue(String value) {
        for (BookingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING_PAYMENT;
    }
}
