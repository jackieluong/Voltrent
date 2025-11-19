package com.hcmut.voltrent.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BaseBankAccountResponse {

    private String id;
    private String accountNumber;
    private String accountName;
    private String bankName;
    private String bankCode;
    private String updatedAt;

    public BaseBankAccountResponse() {
    }
}
