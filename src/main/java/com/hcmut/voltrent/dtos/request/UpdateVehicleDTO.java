package com.hcmut.voltrent.dtos.request;

import com.hcmut.voltrent.constant.VehicleType;
import lombok.Data;

/**
 * Data Transfer Object for updating a vehicle.
 * This prevents clients from updating immutable fields like id or owner email.
 */
@Data
public class UpdateVehicleDTO {

    private String name;
    private VehicleType type;
    private Double pricePerHour;
    private String imageUrl;
    private String brand;
    private String model;
    private String color;
    private String licensePlate;
    private String description;

    private String province;
    private String district;
    private String ward;
    private String address;
}
