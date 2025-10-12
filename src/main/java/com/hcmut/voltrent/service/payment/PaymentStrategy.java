package com.hcmut.voltrent.service.payment;


import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.request.SavePaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;

import java.util.Map;


public interface PaymentStrategy<T extends BasePaymentResponse, R> {

    T executePayment(PaymentRequest paymentRequest);

    R processIPN(Map<String, String> params);

    SavePaymentRequest buildSavePaymentRequest(Map<String, String> params);

}
