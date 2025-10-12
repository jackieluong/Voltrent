package com.hcmut.voltrent.service.payment;


import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;


public interface PaymentStrategy<T extends BasePaymentResponse> {

    T executePayment(PaymentRequest paymentRequest);

}
