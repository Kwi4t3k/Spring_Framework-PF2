package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.model.Rental;
import org.example.model.User;
import org.example.model.Vehicle;
import org.example.repository.RentalRepository;
import org.example.repository.UserRepository;
import org.example.repository.VehicleRepository;
import org.example.service.RentalService;
import org.example.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, VehicleRepository vehicleRepository, UserRepository userRepository, VehicleService vehicleService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.existsByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }

    @Override
    @Transactional
    public Rental rent(String vehicleId, String userId) {
        if(!vehicleService.isAvailable(vehicleId)) {
            throw new IllegalStateException("Vehicle " + vehicleId + " is not available for rent.");
        }
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle consistency error. ID: " + vehicleId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .vehicle(vehicle)
                .user(user)
                .rentDate(LocalDateTime.now())
                .returnDate(null)
                .build();
        Rental savedRental = rentalRepository.save(newRental);
        return savedRental;
    }

    @Override
    public Rental returnRental(String vehicleId, String userId) {
        if(!isVehicleRented(vehicleId)) {
            return null;
        }

        Rental rental = rentalRepository.findByVehicleIdAndUserIdAndReturnDateIsNull(vehicleId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found."));
        rental.setReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);
        return rental;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
