package com.hcmut.voltrent.service.payment;

import com.hcmut.voltrent.config.VNPayConfig;
import com.hcmut.voltrent.constant.PaymentGateway;
import com.hcmut.voltrent.constant.Symbol;
import com.hcmut.voltrent.constant.VNPayParams;
import com.hcmut.voltrent.constant.VnpIpnResponseConst;
import com.hcmut.voltrent.dtos.request.PaymentRequest;
import com.hcmut.voltrent.dtos.request.SavePaymentRequest;
import com.hcmut.voltrent.dtos.response.BasePaymentResponse;
import com.hcmut.voltrent.dtos.response.IpnResponse;
import com.hcmut.voltrent.dtos.response.VNPayResponse;
import com.hcmut.voltrent.service.booking.IBookingService;
import com.hcmut.voltrent.utils.CryptoUtil;
import com.hcmut.voltrent.utils.PaymentUtils;
import jakarta.transaction.Transactional;
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
public class VNPayStrategy implements PaymentStrategy<VNPayResponse, IpnResponse> {

    private final VNPayConfig vnPayConfig;
    private final IBookingService bookingService;

    public VNPayStrategy(VNPayConfig vnPayConfig, IBookingService bookingService) {
        super();
        this.vnPayConfig = vnPayConfig;
        this.bookingService = bookingService;
    }

    @Override
    public SavePaymentRequest buildSavePaymentRequest(Map<String, String> params) {
        var txnRef = params.get(VNPayParams.TXN_REF);
        var bookingId = Long.parseLong(txnRef);
        String responseCode = params.get(VNPayParams.RESPONSE_CODE);

        SavePaymentRequest savePaymentRequest = new SavePaymentRequest();
        savePaymentRequest.setBookingId(String.valueOf(bookingId));
        savePaymentRequest.setGateway(PaymentGateway.VNPAY);
        savePaymentRequest.setTotalAmount(Double.parseDouble(params.get(VNPayParams.AMOUNT)));
        savePaymentRequest.setTransactionId(params.get(VNPayParams.TRANSACTION_NO));
        savePaymentRequest.setPartnerPayDate(params.get(VNPayParams.PAY_DATE));
        savePaymentRequest.setPaymentStatus(PaymentUtils.getPaymentStatusFromVnpayCode(responseCode));
        return savePaymentRequest;

    }

    @Override
    public VNPayResponse executePayment(PaymentRequest request) {

        Long amount = (long) (request.getTotalAmount() * VNPayConfig.DEFAULT_MULTIPLIER);  // 1. amount * 100
        var txnRef = String.valueOf(request.getBookingId());                       // 2. orderId
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

        log.info("[VNPay] paymentUrl: {}", initPaymentUrl);
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
        var bookingId = request.getBookingId();

        return String.format("Thanh toan don hang %s. So tien %s", bookingId, request.getTotalAmount());
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

    /**
     * Cơ chế retry IPN:
     * Hệ thống VNPAY căn cứ theo RspCode phản hồi từ merchant để kết thúc luồng hay bật cơ chế retry
     * RspCode: 00, 02 là mã lỗi IPN của merchant phản hồi đã cập nhật được tình trạng giao dịch. VNPAY kết thúc luồng
     * RspCode: 01, 04, 97, 99 hoặc IPN timeout là mã lỗi IPN merchant không cập nhật được tình trạng giao dịch. VNPAY bật cơ chế retry IPN
     * Tổng số lần gọi tối đa: 10 lần
     * Khoảng cách giữa các lần gọi lại: 5 phút
     *
     */
    @Transactional
    @Override
    public IpnResponse processIPN(Map<String, String> params) {
        if (!verifyIpn(params)) {
            return VnpIpnResponseConst.SIGNATURE_FAILED;
        }

        IpnResponse response = null;
        var txnRef = params.get(VNPayParams.TXN_REF);
        try {
            var bookingId = Long.parseLong(txnRef);
            var responseCode = params.get(VNPayParams.RESPONSE_CODE);
            if (responseCode.equals("00")) {
                bookingService.markBookingAsPaid(bookingId, PaymentGateway.VNPAY);
                response = VnpIpnResponseConst.SUCCESS;
            }

        }
        catch (IllegalArgumentException e) {
            log.error("Error {}", e.getMessage());
            response = VnpIpnResponseConst.ORDER_NOT_FOUND;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            response = VnpIpnResponseConst.UNKNOWN_ERROR;
        }

        log.info("[VNPay Ipn] txnRef: {}, response: {}", txnRef, response);
        return response;
    }
}
