package org.example.services;

import org.example.models.User;

import java.util.List;
import java.util.Optional;

public interface AuthService {
    Optional<User> login(String login, String rawPassword);
    boolean register(String login, String rawPassword, String role);

    List<User> getAllUsers();
}
