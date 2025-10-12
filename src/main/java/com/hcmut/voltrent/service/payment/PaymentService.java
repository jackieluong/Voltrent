package com.hcmut.voltrent.service.payment;

import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.request.SavePaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;
import com.hcmut.voltrent.entity.Payment;
import com.hcmut.voltrent.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

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

    public void savePayment(SavePaymentRequest paymentRequest) {

        Payment payment = Payment.builder()
                .gateway(paymentRequest.getGateway().getValue())
                .bookingId(paymentRequest.getBookingId())
                .totalAmount((long) paymentRequest.getTotalAmount())
                .status(paymentRequest.getPaymentStatus())
                .build();
        try{
            paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Error saving payment {}", payment, e);
            throw new RuntimeException(e);
        }
    }

}
