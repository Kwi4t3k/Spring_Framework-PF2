package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.Rental;
import org.example.repositories.RentalRepository;

import java.sql.*;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
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
                if (rs.next()) return Optional.of(mapResultSetToRental(rs));
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
                if (rs.next()) return Optional.of(mapResultSetToRental(rs));
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
            stmt.setString(2, rental.getVehicleId());
            stmt.setString(3, rental.getUserId());
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

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        return Rental.builder()
                .id(rs.getString("id"))
                .vehicleId(rs.getString("vehicle_id"))
                .userId(rs.getString("user_id"))
                .rentDateTime(rs.getString("rent_date"))
                .returnDateTime(rs.getString("return_date"))
                .build();
    }
}