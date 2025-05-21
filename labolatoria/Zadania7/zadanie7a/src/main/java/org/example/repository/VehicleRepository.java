package org.example.repository;

import org.example.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByIsActiveTrue();
    @Query("SELECT v FROM Vehicle v WHERE v.id = ?1 AND v.isActive = true")
    Optional<Vehicle> findByIdAndIsActiveTrue(String id);
    @Query("SELECT v FROM Vehicle v WHERE v.isActive = true AND v.id NOT IN(?1)")
    List<Vehicle> findByIsActiveTrueAndIdNotIn(Set<String> rentedVehicleIds);
}