package com.hcmut.voltrent.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class VietQRRequest {
    private String accountNo;
    private String accountName;
    private String acqId;
    private long amount;
    private String addInfo;
    private String format;
    private String template;
}
