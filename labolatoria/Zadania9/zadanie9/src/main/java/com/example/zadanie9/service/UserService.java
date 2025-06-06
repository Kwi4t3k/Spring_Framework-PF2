package com.example.zadanie9.service;

import com.example.zadanie9.dto.UserRequest;
import com.example.zadanie9.model.User;

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