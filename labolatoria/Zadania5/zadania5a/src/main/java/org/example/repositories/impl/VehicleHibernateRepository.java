package org.example.repositories.impl;

import lombok.Setter;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@Setter
public class VehicleHibernateRepository implements VehicleRepository {
    Session session;

    public VehicleHibernateRepository(Session session) {
        this.session = session;
    }

    @Override
    public List<Vehicle> findAll() {
        return session.createQuery("from Vehicle", Vehicle.class).list();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(session.get(Vehicle.class, id));
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return session.merge(vehicle);
    }

    @Override
    public void deleteById(String id) {
        Vehicle vehicle = session.get(Vehicle.class, id);
        if (vehicle != null) {
            session.remove(vehicle);
        }
    }
}
