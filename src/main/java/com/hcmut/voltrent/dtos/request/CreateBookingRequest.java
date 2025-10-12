package com.hcmut.voltrent.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {

    @JsonProperty("vehicle_id")
    @NotEmpty(message = "Vehicle id cannot be empty")
    private String vehicleId;

    @NotEmpty(message = "Start time cannot be empty")
    @JsonProperty("start_time")
    private String startTime;

    @NotEmpty(message = "End time cannot be empty")
    @JsonProperty("end_time")
    private String endTime;

    @JsonProperty("total_amount")
    private Long totalAmount;
}
