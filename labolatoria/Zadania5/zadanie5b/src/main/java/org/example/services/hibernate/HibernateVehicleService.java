package org.example.services.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.Vehicle;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.services.VehicleService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HibernateVehicleService implements VehicleService {
    private final VehicleHibernateRepository vehicleRepo;
    private final RentalHibernateRepository rentalRepo;

    public HibernateVehicleService(VehicleHibernateRepository vehicleRepo, RentalHibernateRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo;
        this.rentalRepo = rentalRepo;
    }

    @Override
    public List<Vehicle> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findAll();
        }
    }

    @Override
    public Optional<Vehicle> findById(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            return vehicleRepo.findById(vehicleId);
        }
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);

            return vehicleRepo.findAll().stream()
                    .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty())
                    .toList();
        }
    }

    @Override
    public List<Vehicle> getRentedVehicles(String userId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            vehicleRepo.setSession(session);
            rentalRepo.setSession(session);

            return vehicleRepo.findAll().stream()
                    .filter(v -> rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent())
                    .toList();
        }
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            if (vehicle.getId() == null || vehicle.getId().isBlank()) {
                vehicle.setId(UUID.randomUUID().toString());
            }
            Vehicle savedVehicle = vehicleRepo.save(vehicle);
            tx.commit();
            return savedVehicle;
        }
    }

    @Override
    public void deleteVehicle(String id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            vehicleRepo.setSession(session);
            vehicleRepo.deleteById(id);
            tx.commit();
        }
    }
}
