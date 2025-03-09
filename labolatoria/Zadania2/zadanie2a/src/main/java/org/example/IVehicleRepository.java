package org.example;

import java.util.List;

public interface IVehicleRepository {
    void rentVehicle(String id);
    void returnVehicle(String id);
    List<Vehicle> getVehicles();
    void save();
    void load();
    void addVehicle(Vehicle vehicle);
    void removeVehicle(String id);
}