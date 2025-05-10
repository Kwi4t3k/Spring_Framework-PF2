package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.VehicleRepository;

import java.sql.*;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {
    VehicleRepository vehicleRepo = new VehicleJdbcRepository();
    UserJdbcRepository userRepo = new UserJdbcRepository();

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = vehicleRepo.findById(rs.getString("vehicle_id"))
                        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

                User user = userRepo.findById(rs.getString("user_id"))
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Rental rental = new Rental(
                        rs.getString("id"),
                        vehicle,
                        user,
                        rs.getString("rent_date"),
                        rs.getString("return_date")
                );

                rentals.add(rental);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas pobierania wszystkich wypożyczeń", e);
        }

        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rental WHERE id = ?";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = vehicleRepo.findById(rs.getString("vehicle_id"))
                            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

                    User user = userRepo.findById(rs.getString("user_id"))
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    Rental rental = new Rental(
                            rs.getString("id"),
                            vehicle,
                            user,
                            rs.getString("rent_date"),
                            rs.getString("return_date")
                    );

                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas wyszukiwania wypożyczenia po ID", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = vehicleRepo.findById(rs.getString("vehicle_id"))
                            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

                    User user = userRepo.findById(rs.getString("user_id"))
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    Rental rental = new Rental(
                            rs.getString("id"),
                            vehicle,
                            user,
                            rs.getString("rent_date"),
                            rs.getString("return_date")
                    );

                    return Optional.of(rental);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas wyszukiwania aktywnego wypożyczenia pojazdu", e);
        }

        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isBlank()) {
            rental.setId(UUID.randomUUID().toString());
        } else {
            deleteById(rental.getId());
        }

        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rental.getId());
            stmt.setString(2, rental.getVehicle().getId());
            stmt.setString(3, rental.getUser().getId());
            stmt.setString(4, rental.getRentDateTime());
            stmt.setString(5, rental.getReturnDateTime());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas zapisu wypożyczenia", e);
        }

        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas usuwania wypożyczenia", e);
        }
    }
}