package com.example.zadanie8.service.impl;

import com.example.zadanie8.dto.UserRequest;
import com.example.zadanie8.model.Role;
import com.example.zadanie8.model.User;
import com.example.zadanie8.repository.RoleRepository;
import com.example.zadanie8.repository.UserRepository;
import com.example.zadanie8.service.UserService;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(UserRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Error...");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new IllegalStateException("There is no role... ROLE_USER"));
        User u = User.builder()
                .id(UUID.randomUUID().toString())
                .login(req.getLogin())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(u);
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {

    }

    @Override
    public void removeRoleFromUser(String userId, String roleName) {

    }
}