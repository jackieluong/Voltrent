package com.hcmut.voltrent.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateBookingResponse {
    private String bookingId;
    private String vehicleId;
    private String status;
    private Long totalAmount;
}
