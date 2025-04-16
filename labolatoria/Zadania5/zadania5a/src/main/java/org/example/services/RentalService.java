package org.example.services;

import org.example.models.Rental;

import java.util.List;

public interface RentalService {
    boolean isVehicleRented(String vehicleId);

    Rental rent(String vehicleId, String userId);

    boolean returnRental(String vehicleId, String userId);

    List<Rental> findAll();
}