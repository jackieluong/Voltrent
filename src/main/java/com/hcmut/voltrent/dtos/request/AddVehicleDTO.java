package com.hcmut.voltrent.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for creating a new vehicle.
 */
@Data
@Getter
@Setter
@ToString
public class AddVehicleDTO {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Loại xe không được để trống")
    private String type;

    @NotNull(message = "Giá mỗi giờ không được để trống")
    @Positive(message = "Giá mỗi giờ phải là số dương")
    private Double pricePerHour;

    private String imageUrl;
}
