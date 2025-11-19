package com.hcmut.voltrent.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseTransactionRequest {
    private Long bookingId;
    private double totalAmount;
}
