package com.hcmut.voltrent.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQrResponse {

    private String accountNumber;

    private String accountName;

    private String qrDataUrl;
}
