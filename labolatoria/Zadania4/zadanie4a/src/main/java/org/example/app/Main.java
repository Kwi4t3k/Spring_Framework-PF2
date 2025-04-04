package org.example.app;

import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.jdbc.RentalJdbcRepository;
import org.example.repositories.impl.jdbc.UserJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleJdbcRepository;
import org.example.repositories.impl.json.RentalJsonRepository;
import org.example.repositories.impl.json.UserJsonRepository;
import org.example.repositories.impl.json.VehicleJsonRepository;
import org.example.services.AuthService;
import org.example.services.RentalService;
import org.example.services.VehicleService;

public class Main {
    public static void main(String[] args) {
        String storageType = "jdbc";

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;

        switch (storageType) {
            case "jdbc" -> {
                userRepo = new UserJdbcRepository();
                vehicleRepo = new VehicleJdbcRepository();
                rentalRepo = new RentalJdbcRepository();
            }
            case "json" -> {
                userRepo = new UserJsonRepository();
                vehicleRepo = new VehicleJsonRepository();
                rentalRepo = new RentalJsonRepository();
            }
            default -> throw new IllegalArgumentException("Nieznany typ kontenera " + storageType);
        }

        AuthService authService = new AuthService(userRepo);
        VehicleService vehicleService = new VehicleService(vehicleRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo, userRepo, vehicleRepo);

        App app = new App(authService, vehicleService, rentalService);
        app.run();
    }
}