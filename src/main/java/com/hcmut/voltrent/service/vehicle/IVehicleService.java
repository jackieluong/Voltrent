package com.hcmut.voltrent.service.vehicle;

import com.hcmut.voltrent.dtos.request.UpdateVehicleDTO;
import com.hcmut.voltrent.entity.Vehicle;
import java.util.List;

public interface IVehicleService {
    Vehicle save(Vehicle vehicle);

    List<Vehicle> getMyVehicles(String email);

    Vehicle updateVehicle(Long id, UpdateVehicleDTO vehicleDTO, String email);

    void deleteVehicle(Long id, String email);

    Vehicle pauseVehicle(Long id, String email);

    Vehicle resumeVehicle(Long id, String email);

    List<Vehicle> searchVehicles(String type, Double priceMin, Double priceMax, Double lat, Double lng, Double radius,
            String start, String end);
}
