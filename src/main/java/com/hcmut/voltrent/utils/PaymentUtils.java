package com.hcmut.voltrent.utils;

import com.hcmut.voltrent.constant.PaymentStatus;
import com.hcmut.voltrent.constant.VnpIpnResponseConst;

import java.util.HashMap;
import java.util.Map;

public class PaymentUtils {

    public static Map<String, PaymentStatus> vnPayResultCodeMaps = new HashMap<>();

    static {
        vnPayResultCodeMaps.put(VnpIpnResponseConst.SUCCESS.getResponseCode(), PaymentStatus.SUCCESS);
        vnPayResultCodeMaps.put(VnpIpnResponseConst.UNKNOWN_ERROR.getResponseCode(), PaymentStatus.FAILED);
    }

    public static PaymentStatus getPaymentStatusFromVnpayCode(String resultCode) {
        return vnPayResultCodeMaps.getOrDefault(resultCode, PaymentStatus.FAILED);
    }
}
