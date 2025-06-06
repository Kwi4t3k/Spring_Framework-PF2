package com.example.zadanie9.service;

import com.example.zadanie9.model.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalService {

    boolean isVehicleRented(String vehicleId);

    Optional<Rental> findActiveRentalByVehicleId(String vehicleId);

    Rental rent(String vehicleId, String userId);

    Rental returnRental(String vehicleId, String userId);

    List<Rental> findAll();

    List<Rental> findActiveRentalByUserId(String id);

    List<Rental> findByUserId(String userId);
}