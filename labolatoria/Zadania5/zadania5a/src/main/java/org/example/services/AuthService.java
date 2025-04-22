package org.example.services;

import org.example.models.User;

import java.util.List;
import java.util.Optional;

public interface AuthService {
    boolean register(String login, String rawPassword, String role);

    Optional<User> login(String login, String rawPassword);

    List<User> getAllUsers();
}