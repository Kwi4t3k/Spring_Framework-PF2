package org.example.repository;

import org.example.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId);
    @Query("SELECT r FROM Rental r WHERE r.vehicle.id = ?1 AND r.user.id = ?2 AND r.returnDate IS NULL")
    Optional<Rental> findByVehicleIdAndUserIdAndReturnDateIsNull(String vehicleId, String userId);
    @Query("SELECT true FROM Rental r WHERE r.vehicle.id = ?1 AND r.returnDate IS NULL")
    boolean existsByVehicleIdAndReturnDateIsNull(String vehicleId);
    @Query("SELECT r.vehicle.id FROM Rental r WHERE r.returnDate IS NULL")
    Set<String> findRentedVehicleIds();
}