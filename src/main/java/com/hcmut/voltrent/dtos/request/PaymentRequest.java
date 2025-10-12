package com.hcmut.voltrent.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hcmut.voltrent.constant.PaymentGateway;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    @NotEmpty(message = "Booking id is required")
    @JsonProperty("booking_id")
    protected String bookingId;

    @JsonProperty("total_amount")
    protected double totalAmount;

    @NotNull(message = "Payment method is required")
    protected PaymentGateway gateway;

    protected String ipAddress;

}
