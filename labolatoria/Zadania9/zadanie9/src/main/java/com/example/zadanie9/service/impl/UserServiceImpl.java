package com.example.zadanie9.service.impl;

import com.example.zadanie9.dto.UserRequest;
import com.example.zadanie9.model.Role;
import com.example.zadanie9.model.User;
import com.example.zadanie9.repository.RoleRepository;
import com.example.zadanie9.repository.UserRepository;
import com.example.zadanie9.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public void deleteById(String userId) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        } else {
            user.get().setActive(false);
            user.get().getRoles().clear();
            userRepository.save(user.get());
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String userId) {
        return userRepository.findByIdAndIsActiveTrue(userId);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByIdAndIsActiveTrue(login);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
        user.get().getRoles().add(role);
        userRepository.save(user.get());
    }

    @Override
    public void removeRoleFromUser(String userId, String roleName) {
        Optional<User> user = userRepository.findByIdAndIsActiveTrue(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
        user.get().getRoles().remove(role);
        userRepository.save(user.get());
    }
}