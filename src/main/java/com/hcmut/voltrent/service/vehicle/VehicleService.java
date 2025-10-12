package com.hcmut.voltrent.service.vehicle;

import com.hcmut.voltrent.dtos.request.UpdateVehicleDTO;
import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.constant.VehicleStatus;
import com.hcmut.voltrent.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.hcmut.voltrent.exception.UnauthorizedVehicleAccessException;
import com.hcmut.voltrent.exception.VehicleNotFoundException;
import java.util.UUID;

@Service
public class VehicleService implements IVehicleService {

    private final VehicleRepository vehicleRepository;

    // Using constructor injection is a best practice over field injection
    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
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
    public List<Vehicle> searchVehicles(String type, Double priceMin, Double priceMax) {
        // Ensure that the search parameters are not null to avoid errors.
        String searchType = (type == null) ? "" : type;
        Double min = (priceMin == null) ? 0.0 : priceMin;
        Double max = (priceMax == null) ? Double.MAX_VALUE : priceMax;

        return vehicleRepository.findByTypeContainingAndPricePerHourBetweenAndStatusNot(
                searchType, min, max, VehicleStatus.PAUSED);
    }

    /**
     * A private helper method to find a vehicle by its ID and verify that the
     * user making the request is the owner. This reduces code duplication.
     *
     * @param id         The ID of the vehicle to find.
     * @param ownerIdStr The ID of the owner as a String.
     * @return The Vehicle entity if found and owned by the user.
     * @throws VehicleNotFoundException           if no vehicle with the given ID is
     *                                            found.
     * @throws UnauthorizedVehicleAccessException if the user is not the owner of
     *                                            the vehicle.
     */
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
