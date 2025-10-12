package com.hcmut.voltrent.controller;

import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.dtos.RestResponse;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;
import com.hcmut.voltrent.dtos.response.IpnResponse;
import com.hcmut.voltrent.service.payment.IPaymentService;
import com.hcmut.voltrent.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay_ipn")
    Object processIpn(@RequestParam Map<String, String> params) {
        log.info("[VNPay Ipn] Params: {}", params);

        return paymentService.processIpn(PaymentGateway.VNPAY, params);
    }


    @PostMapping("/checkout")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest paymentRequest, HttpServletRequest httpServletRequest) {

        paymentRequest.setIpAddress(RequestUtil.getIpAddress(httpServletRequest));

        BasePaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        RestResponse result = new RestResponse(HttpStatus.OK.value(), "Create payment url successfully", paymentResponse);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}
