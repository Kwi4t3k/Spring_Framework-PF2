package org.example.services.simple;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.services.RentalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimpleRentalService implements RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public SimpleRentalService(RentalRepository rentalRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    @Override
    public Rental rentVehicle(String vehicleId, String userId) {
        if (isRented(vehicleId)) {
            return null;
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rental rental = new Rental(
                UUID.randomUUID().toString(), vehicle, user, LocalDateTime.now().toString(), null);

        rentalRepository.save(rental);
        return rental;
    }

    @Override
    public Rental returnVehicle(String vehicleId, String userId) {
        if (!isRented(vehicleId)) {
            return null;
        }

        Optional<Rental> rental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);

        if (rental.isPresent()) {
            Rental rentalToReturn = rental.get();

            if (rentalToReturn.getUser().getId().equals(userId)) {
                rentalToReturn.setReturnDateTime(LocalDateTime.now().toString());
                rentalRepository.save(rentalToReturn);
            }
            return rentalToReturn;
        }
        return null;
    }

    @Override
    public boolean isRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}