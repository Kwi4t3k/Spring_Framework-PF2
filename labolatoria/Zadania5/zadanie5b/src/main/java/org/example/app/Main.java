package org.example.app;

import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.repositories.impl.jdbc.RentalJdbcRepository;
import org.example.repositories.impl.jdbc.UserJdbcRepository;
import org.example.repositories.impl.jdbc.VehicleJdbcRepository;
import org.example.repositories.impl.json.RentalJsonRepository;
import org.example.repositories.impl.json.UserJsonRepository;
import org.example.repositories.impl.json.VehicleJsonRepository;
import org.example.services.AuthService;
import org.example.services.RentalService;
import org.example.services.VehicleService;
import org.example.services.hibernate.HibernateAuthService;
import org.example.services.hibernate.HibernateRentalService;
import org.example.services.hibernate.HibernateVehicleService;
import org.example.services.simple.SimpleAuthService;
import org.example.services.simple.SimpleRentalService;
import org.example.services.simple.SimpleVehicleService;

public class Main {
    public static void main(String[] args) {
        String storageType = "hibernate";

        UserRepository userRepo;
        VehicleRepository vehicleRepo;
        RentalRepository rentalRepo;

        AuthService authService;
        RentalService rentalService;
        VehicleService vehicleService;

        switch (storageType) {
            case "jdbc" -> {
                userRepo = new UserJdbcRepository();
                vehicleRepo = new VehicleJdbcRepository();
                rentalRepo = new RentalJdbcRepository();

                authService = new SimpleAuthService(userRepo);
                rentalService = new SimpleRentalService(rentalRepo, userRepo, vehicleRepo);
                vehicleService = new SimpleVehicleService(vehicleRepo, rentalRepo);
            }
            case "json" -> {
                userRepo = new UserJsonRepository();
                vehicleRepo = new VehicleJsonRepository();
                rentalRepo = new RentalJsonRepository();

                authService = new SimpleAuthService(userRepo);
                rentalService = new SimpleRentalService(rentalRepo, userRepo, vehicleRepo);
                vehicleService = new SimpleVehicleService(vehicleRepo, rentalRepo);
            }
            case "hibernate" -> {
                userRepo = new UserHibernateRepository();
                vehicleRepo = new VehicleHibernateRepository();
                rentalRepo = new RentalHibernateRepository();

                authService = new HibernateAuthService((UserHibernateRepository) userRepo);
                rentalService = new HibernateRentalService((RentalHibernateRepository) rentalRepo, (UserHibernateRepository) userRepo, (VehicleHibernateRepository) vehicleRepo);
                vehicleService = new HibernateVehicleService((VehicleHibernateRepository) vehicleRepo, (RentalHibernateRepository) rentalRepo);
            }
            default -> throw new IllegalArgumentException("Nieznany typ kontenera " + storageType);
        }

        App app = new App(authService, vehicleService, rentalService);
        app.run();
    }
}