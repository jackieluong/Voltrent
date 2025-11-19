package com.hcmut.voltrent.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BaseTransactionResponse {
    private String transactionId;
    private double totalAmount;
    private double commissionFee;
    private double ownerAmount;
}
