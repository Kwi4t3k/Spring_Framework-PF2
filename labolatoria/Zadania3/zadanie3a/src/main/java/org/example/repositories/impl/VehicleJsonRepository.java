package org.example.repositories.impl;

import com.google.gson.reflect.TypeToken;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.example.utils.JsonFileStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VehicleJsonRepository implements VehicleRepository {
    private final JsonFileStorage<Vehicle> storage = new JsonFileStorage<>("src/main/resources/vehicles.json", new TypeToken<List<Vehicle>>(){}.getType());

    private final List<Vehicle> vehicles;

    public VehicleJsonRepository() {
        this.vehicles = new ArrayList<>(storage.load());
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(generateNextAvailableId());
        } else {
            deleteById(vehicle.getId());
        }
        vehicles.add(vehicle);
        storage.save(vehicles);
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        vehicles.removeIf(v -> v.getId().equals(id));
        storage.save(vehicles);
    }

    @Override
    public String generateNextAvailableId() {
        int id = 1;

        while (true) {
            String idStr = String.valueOf(id);
            boolean exists = vehicles.stream().anyMatch(v -> v.getId().equals(idStr));
            if (!exists) return idStr;
            id++;
        }
    }
}