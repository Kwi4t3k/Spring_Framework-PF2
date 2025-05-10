package org.example.services;

import org.example.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> findAll();
    Optional<Vehicle> findById(String vehicleId);
    List<Vehicle> getAvailableVehicles();
    List<Vehicle> getRentedVehicles(String userId);
    Vehicle save(Vehicle vehicle);
    void deleteVehicle(String id);
}
