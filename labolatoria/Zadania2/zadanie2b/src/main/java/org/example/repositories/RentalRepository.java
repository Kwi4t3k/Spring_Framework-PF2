package org.example.repositories;

import org.example.models.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    List<Rental> findAll();
    Optional<Rental> findById(String id);
    Optional<Rental> findByVehicleId(String id);
    Optional<Rental> findByUserId(String userId);
    Rental save(Rental rental);
    void deleteById(String id);
}
