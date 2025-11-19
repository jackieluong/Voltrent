package com.hcmut.voltrent.dtos.request;

import com.hcmut.voltrent.constant.VehicleType;
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

    @NotNull(message = "Tên không được để trống")
    private String name;

    private String brand;

    private String model;

    private String color;

    private String licensePlate;

    @NotNull(message = "Loại xe không được để trống")
    private VehicleType type;

    @NotNull(message = "Giá mỗi giờ không được để trống")
    @Positive(message = "Giá mỗi giờ phải là số dương")
    private Double pricePerHour;

    private String description;

    private String imageUrl;

    @NotNull(message = "Tỉnh/Thành phố không được để trống")
    private String province;

    @NotNull(message = "Phường/Xã không được để trống")
    private String ward;

    @NotNull(message = "Địa chỉ không được để trống")
    private String address;
}
