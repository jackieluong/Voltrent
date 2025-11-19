package com.hcmut.voltrent.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseBankAccountRequest {

    private String accountNumber;
    private String accountName;
    private String bankCode;
    private String bankName;
}
