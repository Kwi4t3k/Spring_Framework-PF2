package org.example.services.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.impl.hibernate.RentalHibernateRepository;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.repositories.impl.hibernate.VehicleHibernateRepository;
import org.example.services.RentalService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HibernateRentalService implements RentalService {
    private final RentalHibernateRepository rentalRepo;
    private final UserHibernateRepository userRepo;
    private final VehicleHibernateRepository vehicleRepo;

    public HibernateRentalService(RentalHibernateRepository rentalRepo, UserHibernateRepository userRepo, VehicleHibernateRepository vehicleRepo) {
        this.rentalRepo = rentalRepo;
        this.userRepo = userRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public List<Rental> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findAll();
        }
    }

    @Override
    public Rental rentVehicle(String vehicleId, String userId) {
       Transaction tx = null;
       try (Session session = HibernateConfig.getSessionFactory().openSession()) {
           tx = session.beginTransaction();
           rentalRepo.setSession(session);
           vehicleRepo.setSession(session);
           userRepo.setSession(session);

           if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent()) {
               throw new IllegalStateException("Pojazd jest wypożyczony");
           }

           Vehicle vehicle = vehicleRepo.findById(vehicleId).orElseThrow(() -> new RuntimeException("Nie znaleziono pojazdu"));
           User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

           Rental rental = Rental.builder()
                   .id(UUID.randomUUID().toString())
                   .vehicle(vehicle)
                   .user(user)
                   .rentDateTime(LocalDateTime.now().toString())
                   .build();

           rentalRepo.save(rental);
           tx.commit();
           return rental;
       }
       catch (Exception e) {
           if (tx != null && tx.isActive()) {
               tx.rollback();
           }
           throw e;
       }
    }

    @Override
    public Rental returnVehicle(String vehicleId, String userId) {
        Transaction tx = null;

        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            rentalRepo.setSession(session);
            vehicleRepo.setSession(session);
            userRepo.setSession(session);

            Optional<Rental> rental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId);
            if (rental.isEmpty()) {
                throw new IllegalStateException("Pojazd nie jest wypożyczony");
            }

            Rental rentalToReturn = rental.get();
            if (rentalToReturn.getUser().getId().equals(userId)) {
                rentalToReturn.setReturnDateTime(LocalDateTime.now().toString());
                rentalRepo.save(rentalToReturn);
            }

            tx.commit();
            return rentalToReturn;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean isRented(String vehicleId) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            rentalRepo.setSession(session);
            return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
        }
    }
}
