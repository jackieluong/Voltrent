package com.hcmut.voltrent.dtos.request;

import com.hcmut.voltrent.constant.VehicleType;
import lombok.Data;

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
    private int page = 0;
    private int size = 10;
}
