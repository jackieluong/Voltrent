package com.hcmut.voltrent.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetQRPaymentInfoRequest {

    @NotEmpty(message = "Company id is required")
    private String companyId;
    private long amount;

}
