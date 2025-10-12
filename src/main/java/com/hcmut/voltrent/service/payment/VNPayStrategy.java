package com.hcmut.voltrent.service.payment;

import com.hcmut.voltrent.config.VNPayConfig;
import com.hcmut.voltrent.constant.Symbol;
import com.hcmut.voltrent.constant.VNPayParams;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;
import com.hcmut.voltrent.dtos.response.VNPayResponse;
import com.hcmut.voltrent.utils.CryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@Primary
public class VNPayStrategy implements PaymentStrategy<VNPayResponse> {

    private final VNPayConfig vnPayConfig;

    public VNPayStrategy(VNPayConfig vnPayConfig) {
        super();
        this.vnPayConfig = vnPayConfig;
    }

    @Override
    public VNPayResponse executePayment(PaymentRequest request) {

        Long amount = (long) (request.getTotalAmount() * VNPayConfig.DEFAULT_MULTIPLIER);  // 1. amount * 100
        var txnRef = String.valueOf(request.getOrderId());                       // 2. orderId
        var returnUrl = buildReturnUrl(txnRef);                 // 3. FE redirect by returnUrl

        var ipAddress = request.getIpAddress();
        var orderInfo = buildPaymentDetail(request);

        Map<String, String> params = vnPayConfig.getVNPayConfig();

        params.put(VNPayParams.TXN_REF, txnRef);
        params.put(VNPayParams.RETURN_URL, returnUrl);

        params.put(VNPayParams.IP_ADDRESS, ipAddress);
        params.put(VNPayParams.ORDER_INFO, orderInfo);
        params.put(VNPayParams.AMOUNT, String.valueOf(amount));

        var initPaymentUrl = buildInitPaymentUrl(params);

        return VNPayResponse.builder()
                .vpnUrl(initPaymentUrl)
                .build();
    }

    private String buildInitPaymentUrl(Map<String, String> params) {
        var hashPayload = new StringBuilder();
        var query = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);   // 1. Sort field names

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // 2.1. Build hash data
                hashPayload.append(fieldName);
                hashPayload.append(Symbol.EQUAL);
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // 2.2. Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append(Symbol.EQUAL);
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    query.append(Symbol.AND);
                    hashPayload.append(Symbol.AND);
                }
            }
        }

        // 3. Build secureHash
        var secureHash = CryptoUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashPayload.toString());

        // 4. Finalize query
        query.append("&vnp_SecureHash=");
        query.append(secureHash);

        return vnPayConfig.getInitPaymentPrefixUrl() + "?" + query;
    }

    private String buildPaymentDetail(PaymentRequest request) {
        var orderInfo = request.getOrderId();

        return String.format("Thanh toan don hang %s. So tien %s", orderInfo, request.getTotalAmount());
    }
    private String buildReturnUrl(String orderId) {
        return String.format(vnPayConfig.getReturnUrlFormat(), orderId);
    }

    public boolean verifyIpn(Map<String, String> params) {
        var reqSecureHash = params.get(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH);
        params.remove(VNPayParams.SECURE_HASH_TYPE);
        var hashPayload = new StringBuilder();
        var fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        var itr = fieldNames.iterator();
        while (itr.hasNext()) {
            var fieldName = itr.next();
            var fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashPayload.append(fieldName);
                hashPayload.append(Symbol.EQUAL);
                hashPayload.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (itr.hasNext()) {
                    hashPayload.append(Symbol.AND);
                }
            }
        }

        var secureHash = CryptoUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashPayload.toString());
        return secureHash.equals(reqSecureHash);
    }
}
