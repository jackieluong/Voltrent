package com.hcmut.voltrent.dtos.model;

import com.hcmut.voltrent.constant.VehicleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private Long id;
    private String name;
    private String brand;
    private String model;
    private String color;
    private String licensePlate;
    private String description;
    private String type;
    private boolean isPaused;
    private VehicleStatus status;
    private String imageUrl;

}
