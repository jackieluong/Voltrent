package com.hcmut.voltrent.dtos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BankInfo {
    private String accountNumber;
    private String accountName;
    private String bankName;
    private String bankCode;
}
