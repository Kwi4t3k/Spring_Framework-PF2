package org.example.services.simple;

import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.VehicleService;

import java.util.*;

public class SimpleVehicleService implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;

    public SimpleVehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<Vehicle> availableVehicles = new ArrayList<>();

        if (allVehicles.isEmpty()) {
            System.err.println("Brak pojazdów w wypożyczalni");
        } else {
            allVehicles.forEach(v -> {
                if (rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty()) {
                    availableVehicles.add(v);
                }
            });
        }
        return availableVehicles;
    }

    @Override
    public List<Vehicle> getRentedVehicles(String userId) {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        List<Vehicle> rentedVehicles = new ArrayList<>();

        if (allVehicles.isEmpty()) {
            System.err.println("Brak pojazdów w wypożyczalni");
        } else {
            allVehicles.forEach(v -> {
                if (rentalRepository.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent()) {
                    rentedVehicles.add(v);
                }
            });
        }
        return rentedVehicles;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(String id) {
        vehicleRepository.deleteById(id);
    }
}