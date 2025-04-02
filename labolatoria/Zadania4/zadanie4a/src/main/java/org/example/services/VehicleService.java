package org.example.services;

import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    public boolean deleteVehicle(String id) {
        Optional<Vehicle> existing = vehicleRepository.findById(id);
        if (existing.isPresent()) {
            vehicleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        List<String> rentedIds = rentalRepository.findAll().stream()
                .filter(r -> r.getReturnDateTime() == null)
                .map(Rental::getVehicleId)
                .collect(Collectors.toList());

        return vehicleRepository.findAll().stream()
                .filter(vehicle -> !rentedIds.contains(vehicle.getId()))
                .collect(Collectors.toList());
    }

    public List<Vehicle> getRentedVehicles() {
        List<String> rentedIds = rentalRepository.findAll().stream()
                .filter(r -> r.getReturnDateTime() == null)
                .map(Rental::getVehicleId)
                .collect(Collectors.toList());

        return vehicleRepository.findAll().stream()
                .filter(vehicle -> rentedIds.contains(vehicle.getId()))
                .collect(Collectors.toList());
    }
}