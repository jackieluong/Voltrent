package com.hcmut.voltrent.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfirmTransferResponse {
    private String bookingId;
    private String message;
    private String status;
}
