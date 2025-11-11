package com.hcmut.voltrent.service.vehicle;

import com.hcmut.voltrent.constant.BookingStatus;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.dtos.request.UpdateVehicleDTO;
import com.hcmut.voltrent.dtos.request.VehicleFilterRequest;
import com.hcmut.voltrent.dtos.response.PagedResponse;
import com.hcmut.voltrent.entity.Booking;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.repository.BookingRepository;
import com.hcmut.voltrent.repository.VehicleRepository;
import com.hcmut.voltrent.repository.VehicleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
        UUID ownerId = UUID.fromString(ownerIdStr);
        return vehicleRepository.findByOwnerId(ownerId);
    }

    @Override
    public Vehicle updateVehicle(Long id, UpdateVehicleDTO vehicleDTO, String ownerIdStr) {
        Vehicle existingVehicle = findAndVerifyOwnership(id, ownerIdStr);

        // Update fields from the DTO
        existingVehicle.setName(vehicleDTO.getName());
        existingVehicle.setType(vehicleDTO.getType());
        existingVehicle.setPricePerHour(vehicleDTO.getPricePerHour());
        existingVehicle.setImageUrl(vehicleDTO.getImageUrl());

        // NEW: map additional searchable/updatable attributes
        existingVehicle.setBrand(vehicleDTO.getBrand());
        existingVehicle.setModel(vehicleDTO.getModel());
        existingVehicle.setColor(vehicleDTO.getColor());
        existingVehicle.setLicensePlate(vehicleDTO.getLicensePlate());
        existingVehicle.setDescription(vehicleDTO.getDescription());
        existingVehicle.setProvince(vehicleDTO.getProvince());
        existingVehicle.setDistrict(vehicleDTO.getDistrict());
        existingVehicle.setWard(vehicleDTO.getWard());
        existingVehicle.setAddress(vehicleDTO.getAddress());

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
    public PagedResponse<Vehicle> searchVehicles(VehicleFilterRequest request) {
        Specification<Vehicle> spec = Specification.where(VehicleSpecification.hasType(request.getType()))
                .and(VehicleSpecification.hasProvince(request.getProvince()))
                .and(VehicleSpecification.hasDistrict(request.getDistrict()))
                .and(VehicleSpecification.hasWard(request.getWard()))
                .and(VehicleSpecification.hasAddress(request.getAddress()))
                .and(VehicleSpecification.hasPriceBetween(request.getPriceMin(), request.getPriceMax()));

        Sort sort; // Default sort
        if (request.getSort() != null && !request.getSort().isEmpty()) {
            switch (request.getSort()) {
                case "price_asc":
                    sort = Sort.by(Sort.Direction.ASC, "pricePerHour");
                    break;
                case "price_desc":
                    sort = Sort.by(Sort.Direction.DESC, "pricePerHour");
                    break;
                case "updated_desc":
                    sort = Sort.by(Sort.Direction.DESC, "updatedAt");
                    break;
                case "default":
                default:
                    sort = Sort.by(Sort.Order.asc("typeSortOrder"), Sort.Order.desc("updatedAt"));
                    break;
            }
        } else {
            sort = Sort.by(Sort.Order.asc("typeSortOrder"), Sort.Order.desc("updatedAt"));
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);

        return new PagedResponse<>(vehiclePage.getContent(), vehiclePage.getNumber(),
                vehiclePage.getSize(), vehiclePage.getTotalElements(), vehiclePage.getTotalPages());
    }

    private Vehicle findAndVerifyOwnership(Long id, String ownerIdStr) {
        UUID ownerId = UUID.fromString(ownerIdStr);
        // Find the vehicle or throw an exception if it doesn't exist.
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle with ID '" + id + "' not found."));

        // Check if the owner's ID of the vehicle matches the provided owner ID.
        if (!vehicle.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedVehicleAccessException("You do not have permission to modify this vehicle.");
        }

        return vehicle;
    }
}
