package com.hcmut.voltrent.config;

import com.hcmut.voltrent.constant.Currency;
import com.hcmut.voltrent.constant.Locale;
import com.hcmut.voltrent.constant.VNPayParams;
import com.hcmut.voltrent.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Getter
public class VNPayConfig {
    public static final String VERSION = "2.1.0";
    public static final String COMMAND = "pay";
    public static final String ORDER_TYPE = "190000";
    public static final long DEFAULT_MULTIPLIER = 100L;

    @Value("${payment.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${payment.vnpay.init-payment-url}")
    private String initPaymentPrefixUrl;

    @Value("${payment.vnpay.return-url}")
    private String returnUrlFormat;

    @Value("${payment.vnpay.timeout}")
    private Integer paymentTimeout;

    @Value("${payment.vnpay.secret-key}")
    private String secretKey;


    @PostConstruct
    public void init() {
        System.out.println("tmnCode: " + tmnCode);
        System.out.println("initPaymentPrefixUrl: " + initPaymentPrefixUrl);
        System.out.println("returnUrlFormat: " + returnUrlFormat);
        System.out.println("paymentTimeout: " + paymentTimeout);
        System.out.println("secretKey: " + secretKey);
    }

    public Map<String, String> getVNPayConfig() {

        var vnCalendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        var createdDate = DateUtils.formatVnTime(vnCalendar);
        vnCalendar.add(Calendar.MINUTE, paymentTimeout);
        var expiredDate = DateUtils.formatVnTime(vnCalendar);    // 4. expiredDate for secure


//        var requestId = request.getRequestId();
        Map<String, String> params = new HashMap<>();

        params.put(VNPayParams.VERSION, VERSION);
        params.put(VNPayParams.COMMAND, COMMAND);
        params.put(VNPayParams.TMN_CODE, tmnCode);
        params.put(VNPayParams.CURRENCY, Currency.VND.getValue());
        params.put(VNPayParams.CREATED_DATE, createdDate);
        params.put(VNPayParams.EXPIRE_DATE, expiredDate);
        params.put(VNPayParams.LOCALE, Locale.VIETNAM.getCode());
        params.put(VNPayParams.ORDER_TYPE, ORDER_TYPE);

        return params;
    }
}
