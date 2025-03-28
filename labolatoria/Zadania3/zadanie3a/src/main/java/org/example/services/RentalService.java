package org.example.services;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public boolean rentVehicle(User user, Vehicle vehicle) {
        if (vehicle.isRented()) {
            return false;
        }

        vehicle.setRented(true);
        vehicleRepository.save(vehicle);

        Rental rental = Rental.builder()
                .vehicleId(vehicle.getId())
                .userId(user.getId())
                .rentDateTime(LocalDateTime.now().toString())
                .build();

        rentalRepository.save(rental);
        return true;
    }

    public boolean returnVehicle(User user) {
        Optional<Rental> rentalOpt = rentalRepository.findByUserId(user.getId());

        if (rentalOpt.isEmpty()) {
            return false;
        }

        Rental rental = rentalOpt.get();
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId()).orElse(null);
        if (vehicle == null) {
            return false;
        }

        vehicle.setRented(false);
        vehicleRepository.save(vehicle);

        rental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(rental);
        rentalRepository.deleteById(rental.getId());

        return true;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> getUserRental(User user) {
        return rentalRepository.findByUserId(user.getId());
    }
}