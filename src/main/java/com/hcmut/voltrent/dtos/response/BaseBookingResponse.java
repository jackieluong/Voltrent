package com.hcmut.voltrent.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Super;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseBookingResponse {
    @JsonProperty("booking_id")
    private String bookingId;
    @JsonProperty("vehicle_id")
    private String vehicleId;
    private String status;

    @JsonProperty("total_amount")
    private double totalAmount;
}
