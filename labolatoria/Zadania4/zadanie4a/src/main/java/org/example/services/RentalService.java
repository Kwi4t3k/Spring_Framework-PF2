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
        Optional<Rental> activeRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicle.getId());
        if (activeRental.isPresent()) {
            return false;
        }

        Rental rental = Rental.builder()
                .id(null)
                .userId(user.getId())
                .vehicleId(vehicle.getId())
                .rentDateTime(LocalDateTime.now().toString())
                .returnDateTime(null)
                .build();

        rentalRepository.save(rental);
        return true;
    }

    public boolean returnVehicle(User user) {
        Optional<Rental> rentalOpt = rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(user.getId()))
                .filter(r -> r.getReturnDateTime() == null)
                .findFirst();

        if (rentalOpt.isEmpty()) {
            return false;
        }

        Rental rental = rentalOpt.get();
        rental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(rental); // aktualizujemy wypożyczenie z datą zwrotu

        return true;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> getUserRental(User user) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(user.getId()))
                .filter(r -> r.getReturnDateTime() == null)
                .findFirst();
    }

    public Optional<Rental> getRentalByVehicleId(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId);
    }
}