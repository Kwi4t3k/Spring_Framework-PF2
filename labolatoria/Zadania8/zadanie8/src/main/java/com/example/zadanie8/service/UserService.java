package com.example.zadanie8.service;

import com.example.zadanie8.dto.UserRequest;
import com.example.zadanie8.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByLogin(String login);

    void addRoleToUser(String userId, String roleName);

    void removeRoleFromUser(String userId, String roleName);

    void register(UserRequest req);

    void deleteById(String id);
}