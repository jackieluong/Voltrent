package com.hcmut.voltrent.dtos.request;

import com.hcmut.voltrent.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SavePaymentRequest extends PaymentRequest{

    // transactionId is used for VNPay payment, crucial to query the transaction
    private String transactionId;

    // sessionId is used for Stripe payment, crucial to retrieve checkout session
    private String sessionId;

    private String partnerPayDate;

    private PaymentStatus paymentStatus;
}
