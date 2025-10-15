package com.hcmut.voltrent.dtos.response;

import com.hcmut.voltrent.entity.Vehicle;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentedVehicleDto {
    private Vehicle vehicle;
    private String bookingStatus;
    private Long bookingId;
    private String startTime;
    private String endTime;
}
