package com.hcmut.voltrent.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPaymentRequest {

    @NotEmpty(message = "Action cannot be empty")
    private String action;
    private String note;
}
