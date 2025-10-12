package com.hcmut.voltrent.constant;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(2,"PENDING"),
    SUCCESS(0,"SUCCESS"),
    FAILED(-1,"FAILED");

    private final int resultCode;
    private final String description;


    PaymentStatus(int resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public PaymentStatus fromResultCode(int resultCode) {
        for (PaymentStatus status : values()) {
            if (status.getResultCode() == resultCode) {
                return status;
            }
        }
        return null;
    }

}
