package com.hcmut.voltrent.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfirmPaymentResponse {
    private String bookingId;
    private String status;
    private String paymentStatus;
    private String transactionId;
    private String updatedAt;
}
