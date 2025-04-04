package org.example.repositories.impl.jdbc;

import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.db.JdbcConnectionManager;
import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;


public class VehicleJdbcRepository implements VehicleRepository {

    private final Gson gson = new Gson();

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String attrJson = rs.getString("attributes");
                Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>() {
                }.getType());

                Vehicle vehicle = Vehicle.builder()
                        .id(rs.getString("id"))
                        .category(rs.getString("category"))
                        .brand(rs.getString("brand"))
                        .model(rs.getString("model"))
                        .year(rs.getInt("year"))
                        .plate(rs.getString("plate"))
                        .price(rs.getDouble("price"))
                        .attributes(attributes != null ? attributes : new HashMap<>())
                        .build();
                list.add(vehicle);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        }
        return list;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        String sql = "SELECT * FROM vehicle WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String attrJson = rs.getString("attributes");
                    Map<String, Object> attributes = gson.fromJson(attrJson, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    Vehicle vehicle = Vehicle.builder()
                            .id(rs.getString("id"))
                            .category(rs.getString("category"))
                            .brand(rs.getString("brand"))
                            .model(rs.getString("model"))
                            .year(rs.getInt("year"))
                            .plate(rs.getString("plate"))
                            .price(rs.getDouble("price"))
                            .attributes(attributes != null ? attributes : new HashMap<>())
                            .build();
                    return Optional.of(vehicle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle", e);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
        }

        Optional<Vehicle> existingVehicle = findById(vehicle.getId());

        if (existingVehicle.isPresent()) {
            String sql = "UPDATE vehicle SET category = ?, brand = ?, model = ?, year = ?, plate = ?, price = ?, attributes = ?::jsonb WHERE id = ?";

            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, vehicle.getCategory());
                stmt.setString(2, vehicle.getBrand());
                stmt.setString(3, vehicle.getModel());
                stmt.setInt(4, vehicle.getYear());
                stmt.setString(5, vehicle.getPlate());
                stmt.setDouble(6, vehicle.getPrice());
                stmt.setString(7, gson.toJson(vehicle.getAttributes()));
                stmt.setString(8, vehicle.getId());  // Zmieniamy pojazd o tym ID

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd podczas aktualizacji pojazdu", e);
            }

        } else {
            String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate, price, attributes) VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)";

            try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, vehicle.getId());
                stmt.setString(2, vehicle.getCategory());
                stmt.setString(3, vehicle.getBrand());
                stmt.setString(4, vehicle.getModel());
                stmt.setInt(5, vehicle.getYear());
                stmt.setString(6, vehicle.getPlate());
                stmt.setDouble(7, vehicle.getPrice());
                stmt.setString(8, gson.toJson(vehicle.getAttributes()));

                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Błąd podczas dodawania pojazdu", e);
            }
        }

        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection connection = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        }
    }
}

