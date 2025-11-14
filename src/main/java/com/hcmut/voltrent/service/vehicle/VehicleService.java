package com.hcmut.voltrent.service.vehicle;

import com.hcmut.voltrent.constant.BookingStatus;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.dtos.request.UpdateVehicleDTO;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.hcmut.voltrent.exception.UnauthorizedVehicleAccessException;
import com.hcmut.voltrent.exception.VehicleNotFoundException;

@Service
public class VehicleService implements IVehicleService {
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    // Using constructor injection is a best practice over field injection
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, BookingRepository bookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        // When creating a new vehicle, ensure its status is set to AVAILABLE.
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getMyVehicles(String ownerIdStr) {
        return vehicleRepository.findByOwnerId(ownerIdStr);
    }

    @Override
    public Vehicle updateVehicle(Long id, UpdateVehicleDTO vehicleDTO, String ownerIdStr) {
        Vehicle existingVehicle = findAndVerifyOwnership(id, ownerIdStr);

        // Update fields from the DTO
        existingVehicle.setName(vehicleDTO.getName());
        existingVehicle.setType(vehicleDTO.getType());
        existingVehicle.setPricePerHour(vehicleDTO.getPricePerHour());
        existingVehicle.setImageUrl(vehicleDTO.getImageUrl());

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(Long id, String ownerIdStr) {
        Vehicle vehicleToDelete = findAndVerifyOwnership(id, ownerIdStr);
        vehicleRepository.delete(vehicleToDelete);
    }

    @Override
    public Vehicle pauseVehicle(Long id, String ownerIdStr) {
        Vehicle existingVehicle = findAndVerifyOwnership(id, ownerIdStr);
        // Set the status to PAUSED to make it temporarily unavailable for rent
        existingVehicle.setStatus(VehicleStatus.PAUSED);
        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public Vehicle resumeVehicle(Long id, String ownerIdStr) {
        Vehicle existingVehicle = findAndVerifyOwnership(id, ownerIdStr);
        // Set the status back to AVAILABLE
        existingVehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public List<Vehicle> searchVehicles(String type, Double priceMin, Double priceMax, Double lat, Double lng,
            Double radius, String start, String end) {
        // 1. Lấy tất cả xe không bị pause
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> !v.isPaused())
                .collect(Collectors.toList());

        // 2. Lọc theo type, priceMin, priceMax
        if (type != null && !type.isBlank()) {
            vehicles = vehicles.stream().filter(v -> type.equalsIgnoreCase(v.getType())).collect(Collectors.toList());
        }
        if (priceMin != null) {
            vehicles = vehicles.stream().filter(v -> v.getPricePerHour() >= priceMin).collect(Collectors.toList());
        }
        if (priceMax != null) {
            vehicles = vehicles.stream().filter(v -> v.getPricePerHour() <= priceMax).collect(Collectors.toList());
        }

        // 3. Lọc theo vị trí nếu có
        if (lat != null && lng != null && radius != null) {
            vehicles = vehicles.stream().filter(v -> {
                if (v.getLatitude() == null || v.getLongitude() == null)
                    return false;
                double dist = distance(lat, lng, v.getLatitude(), v.getLongitude());
                return dist <= radius;
            }).collect(Collectors.toList());
        }

        // 4. Loại trừ xe đã được booking confirmed giao nhau thời gian [start, end)
        if (start != null && end != null) {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED.getValue());
            List<Long> unavailableVehicleIds = bookings.stream()
                    .filter(b -> {
                        LocalDateTime bStart = LocalDateTime.parse(b.getStartTime());
                        LocalDateTime bEnd = LocalDateTime.parse(b.getEndTime());
                        // Giao nhau: !(bEnd <= startTime || bStart >= endTime)
                        return !(bEnd.isBefore(startTime) || bEnd.equals(startTime) || bStart.isAfter(endTime)
                                || bStart.equals(endTime));
                    })
                    .map(b -> Long.valueOf(b.getVehicleId()))
                    .distinct()
                    .collect(Collectors.toList());
            vehicles = vehicles.stream().filter(v -> !unavailableVehicleIds.contains(v.getId()))
                    .collect(Collectors.toList());
        }

        return vehicles;
    }

    // Haversine formula for distance in km
    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Vehicle findAndVerifyOwnership(Long id, String ownerIdStr) {

        // Find the vehicle or throw an exception if it doesn't exist.
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with ID '" + id + "' not found."));

        // Check if the owner's ID of the vehicle matches the provided owner ID.
        if (!vehicle.getOwnerId().equals(ownerIdStr)) {
            throw new UnauthorizedVehicleAccessException("You do not have permission to modify this vehicle.");
        }

        return vehicle;
    }
}
