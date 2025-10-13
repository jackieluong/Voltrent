package com.hcmut.voltrent.service.payment;

import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.request.SavePaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;
import com.hcmut.voltrent.entity.Payment;
import com.hcmut.voltrent.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class PaymentService implements IPaymentService {

    private final VNPayStrategy vnPayStrategy;
    private final PaymentRepository paymentRepository;

    public PaymentService(VNPayStrategy vnPayStrategy, PaymentRepository paymentRepository) {
        this.vnPayStrategy = vnPayStrategy;
        this.paymentRepository = paymentRepository;
    }

    private PaymentStrategy getPaymentStrategy(PaymentGateway paymentMethod){

        return switch (paymentMethod) {
            case CoD -> null;
            case VNPAY -> vnPayStrategy;
            case MOMO -> null;
            default -> throw new RuntimeException("Unsupported payment method");
        };

    }
    public BasePaymentResponse createPayment(PaymentRequest paymentRequest){

        PaymentStrategy paymentStrategy = this.getPaymentStrategy(paymentRequest.getGateway());
        if(paymentStrategy == null){
            log.error("Unsupported payment gateway: {}", paymentRequest.getGateway());
            throw new RuntimeException("Unsupported payment gateway");
        }

        return paymentStrategy.executePayment(paymentRequest);

    }

    @Override
    public Object processIpn(PaymentGateway paymentGateway, Map<String, String> params) {
        log.info("[Payment Ipn] gateway: {}, params: {}", paymentGateway, params);

        PaymentStrategy paymentStrategy = this.getPaymentStrategy(paymentGateway);

        Object ipnResponse = paymentStrategy.processIPN(params);
        SavePaymentRequest savePaymentRequest = paymentStrategy.buildSavePaymentRequest(params);

        this.savePayment(savePaymentRequest);
        return ipnResponse;
    }


    public void savePayment(SavePaymentRequest paymentRequest) {

        Payment payment = Payment.builder()
                .gateway(paymentRequest.getGateway().getValue())
                .bookingId(paymentRequest.getBookingId())
                .totalAmount(paymentRequest.getTotalAmount())
                .transactionRef(paymentRequest.getTransactionId())
                .status(paymentRequest.getPaymentStatus().getDescription())
                .partnerPayDate(paymentRequest.getPartnerPayDate())
                .partnerCode(paymentRequest.getPartnerCode())
                .build();
        try{
            log.info("Saving payment {}", payment);
            paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Error saving payment {}", payment, e);
//            throw new RuntimeException(e);
        }
    }

}
