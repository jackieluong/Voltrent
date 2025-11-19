package com.hcmut.voltrent.dtos.response;

import com.hcmut.voltrent.dtos.model.BankInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetBookingQRInfo {
    private String bookingId;
    private double amount;
    private BankInfo bankInfo;
    private String transferContent;
    private String note;
    private String template;

}
