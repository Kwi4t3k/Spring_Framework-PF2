package org.example.services;

import org.example.models.Rental;

import java.util.List;

public interface RentalService {
    List<Rental> findAll();
    Rental rentVehicle(String vehicleId, String userId);
    Rental returnVehicle(String vehicleId, String userId);
    boolean isRented(String vehicleId);
}
