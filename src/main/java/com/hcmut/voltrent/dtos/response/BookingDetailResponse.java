package com.hcmut.voltrent.dtos.response;


import com.hcmut.voltrent.dtos.model.UserDto;
import com.hcmut.voltrent.dtos.model.VehicleDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailResponse extends BaseBookingResponse {

    private String startDate;
    private String endDate;
    private UserDto renter;
    private VehicleDto vehicle;
}
