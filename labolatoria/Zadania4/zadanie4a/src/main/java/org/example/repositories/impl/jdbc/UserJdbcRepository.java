package org.example.repositories.impl.jdbc;

import org.example.db.JdbcConnectionManager;
import org.example.models.User;
import org.example.repositories.UserRepository;

import java.sql.*;
import java.util.*;

public class UserJdbcRepository implements UserRepository {

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = User.builder()
                        .id(rs.getString("id"))
                        .login(rs.getString("login"))
                        .password(rs.getString("password"))
                        .role(rs.getString("role"))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas wczytywania użytkowników", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .password(rs.getString("password"))
                            .role(rs.getString("role"))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas wyszukiwania użytkownika po ID", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = User.builder()
                            .id(rs.getString("id"))
                            .login(rs.getString("login"))
                            .password(rs.getString("password"))
                            .role(rs.getString("role"))
                            .build();
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas wyszukiwania użytkownika po loginie", e);
        }

        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        } else {
            deleteById(user.getId());
        }

        String sql = "INSERT INTO users (id, login, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas zapisu użytkownika", e);
        }

        return user;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = JdbcConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Błąd podczas usuwania użytkownika", e);
        }
    }
}