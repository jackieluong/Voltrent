package com.hcmut.voltrent.constant;

/**
 * Represents the status of a vehicle.
 */
public enum VehicleStatus {
    AVAILABLE, // The vehicle is available for rent.
    PAUSED, // The owner has temporarily made the vehicle unavailable.
    RENTED, // The vehicle is currently being rented by a customer.
    MAINTENANCE // The vehicle is undergoing maintenance.
}
