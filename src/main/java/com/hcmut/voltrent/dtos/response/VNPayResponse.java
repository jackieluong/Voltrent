package com.hcmut.voltrent.dtos.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VNPayResponse extends BasePaymentResponse{

    private String vpnUrl;

}
