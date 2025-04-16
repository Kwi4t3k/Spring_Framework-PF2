package org.example.services.impl;

import org.example.config.HibernateConfig;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.repositories.impl.RentalHibernateRepository;
import org.example.repositories.impl.VehicleHibernateRepository;
import org.example.services.VehicleService;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleHibernateService implements VehicleService {
    private final VehicleHibernateRepository vehicleHibernateRepository;
    private final RentalHibernateRepository rentalHibernateRepository;

    public VehicleHibernateService(VehicleHibernateRepository vehicleHibernateRepository, RentalHibernateRepository rentalHibernateRepository) {
        this.vehicleHibernateRepository = vehicleHibernateRepository;
        this.rentalHibernateRepository = rentalHibernateRepository;
    }

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleHibernateRepository.setSession(session);
            return vehicleHibernateRepository.findAll();
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleHibernateRepository.setSession(session);
            return vehicleHibernateRepository.findById(id);
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            vehicleHibernateRepository.setSession(session);
            Vehicle savedVehicle = vehicleHibernateRepository.save(vehicle);
            session.getTransaction().commit();
            return savedVehicle;
        }
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
       try (Session session = HibernateConfig.getSessionFactory().openSession()) {
           vehicleHibernateRepository.setSession(session);
           rentalHibernateRepository.setSession(session);

           List<Vehicle> vehicles = vehicleHibernateRepository.findAll();
           List<Rental> rentals = rentalHibernateRepository.findAll();

           Set<String> rentedVehiclesIds = rentals
                   .stream()
                   .filter(o -> o.getReturnDate() == null)
                   .map(o -> o.getVehicle().getId())
                   .collect(Collectors.toSet());

           return vehicles
                   .stream()
                   .filter(vehicle -> !rentedVehiclesIds.contains(vehicle.getId()))
                   .toList();
       }
    }

    @Override
    public boolean isAvailable(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalHibernateRepository.setSession(session);
            return rentalHibernateRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isEmpty();
        }
    }

    @Override
    public void deleteById(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            vehicleHibernateRepository.setSession(session);
            vehicleHibernateRepository.deleteById(id);
            session.getTransaction().commit();
        }
    }
}
