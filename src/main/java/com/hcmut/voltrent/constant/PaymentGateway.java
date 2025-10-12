package com.hcmut.voltrent.constant;

import lombok.Getter;

@Getter
public enum PaymentGateway {
    VNPAY("VNPAY"),
    PAYOS("PAYOS"),
    CoD("CoD"),
    MOMO("MOMO");

    private final String value;

    PaymentGateway(String value) {
        this.value = value;
    }
}
