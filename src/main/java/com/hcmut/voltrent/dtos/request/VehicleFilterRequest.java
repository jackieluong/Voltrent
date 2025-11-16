package com.hcmut.voltrent.dtos.request;

import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.constant.VehicleType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class VehicleFilterRequest {
    private VehicleType type;
    private String province;
    private String district;
    private String ward;
    private String address;
    private Integer priceMin;
    private Integer priceMax;
    private String sort;
    private VehicleStatus status;
    private int page = 0;
    private int size = 10;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endTime;
}
