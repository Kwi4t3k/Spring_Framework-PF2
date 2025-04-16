package org.example.app;

import org.example.config.HibernateConfig;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.UserHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.example.services.impl.AuthHibernateService;
import org.example.services.impl.RentalHibernateService;
import org.example.services.impl.VehicleHibernateService;
import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {
        Session session = HibernateConfig.getSessionFactory().openSession();

        VehicleHibernateRepository vehicleRepo = new VehicleHibernateRepository(session);
        RentalHibernateRepository rentalRepo = new RentalHibernateRepository(session);
        UserHibernateRepository userRepo = new UserHibernateRepository(session);

        VehicleHibernateService vehicleService = new VehicleHibernateService(vehicleRepo, rentalRepo);
        RentalHibernateService rentalService = new RentalHibernateService(rentalRepo, vehicleRepo, userRepo);
        AuthHibernateService authService = new AuthHibernateService(userRepo);

        App app = new App(authService, vehicleService, rentalService);
        app.run();
    }
}