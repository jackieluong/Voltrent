package com.hcmut.voltrent.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateBookingResponse {
    @JsonProperty("booking_id")
    private String bookingId;
    @JsonProperty("vehicle_id")
    private String vehicleId;
    private String status;

    @JsonProperty("total_amount")
    private double totalAmount;
}
