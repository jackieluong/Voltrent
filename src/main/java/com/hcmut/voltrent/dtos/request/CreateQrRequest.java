package com.hcmut.voltrent.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQrRequest {

    @NotEmpty(message = "Account number is required")
    private String accountNumber;

    @NotEmpty(message = "Account name is required")
    private String accountName;

    private String bankCode;
}
