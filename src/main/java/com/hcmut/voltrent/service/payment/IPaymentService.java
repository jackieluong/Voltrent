package com.hcmut.voltrent.service.payment;

import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.request.SavePaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;

import java.util.Map;

public interface IPaymentService {

    /**
     * Create and execute a payment based on the specified gateway and request data.
     *
     * @param paymentRequest contains payment details and gateway info.
     * @return the result of the payment creation.
     */
    BasePaymentResponse createPayment(PaymentRequest paymentRequest);

    Object processIpn(PaymentGateway paymentGateway, Map<String, String> params);
}
