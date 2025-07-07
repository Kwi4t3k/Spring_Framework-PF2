package com.example.store.service;

import com.example.store.dto.UserRequest;
import com.example.store.model.User;

import java.util.Optional;

public interface UserService {
    void addRoleToUser(String userId, String roleName);

    void register(UserRequest req);

    Optional<User> findByLogin(String login);
}
