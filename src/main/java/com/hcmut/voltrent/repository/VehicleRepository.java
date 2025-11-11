package com.hcmut.voltrent.repository;

import com.hcmut.voltrent.entity.Vehicle;
import com.hcmut.voltrent.constant.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {
    List<Vehicle> findByOwnerId(UUID ownerId);

    List<Vehicle> findByTypeContainingAndPricePerHourBetween(String type, Double priceMin, Double priceMax);

}
