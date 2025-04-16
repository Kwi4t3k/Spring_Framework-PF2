package org.example.app;

import org.example.services.impl.AuthHibernateService;
import org.example.services.impl.RentalHibernateService;
import org.example.services.impl.VehicleHibernateService;

import java.util.Scanner;

public class App {
    private final AuthHibernateService authHibernateService;
    private final VehicleHibernateService vehicleHibernateService;
    private final RentalHibernateService rentalHibernateService;
    private final Scanner scanner = new Scanner(System.in);

    public App(AuthHibernateService authHibernateService, VehicleHibernateService vehicleHibernateService, RentalHibernateService rentalHibernateService) {
        this.authHibernateService = authHibernateService;
        this.vehicleHibernateService = vehicleHibernateService;
        this.rentalHibernateService = rentalHibernateService;
    }
}
