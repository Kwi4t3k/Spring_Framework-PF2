package com.example.store.service.impl;

import com.example.store.dto.UserRequest;
import com.example.store.model.Cart;
import com.example.store.model.Order;
import com.example.store.model.Role;
import com.example.store.model.User;
import com.example.store.repository.*;
import com.example.store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public User register(UserRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        User u = User.builder()
                .id(UUID.randomUUID().toString())
                .login(req.getLogin())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(new HashSet<>())
                .build();

        u.getRoles().add(userRole);
        User savedUser = userRepository.save(u);

        Cart cart = Cart.builder()
                .id(UUID.randomUUID().toString())
                .user(savedUser)
                .build();

        cartRepository.save(cart);
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void unban(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with ID: " + userId + " not found"));
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void addRoleToUser(String userId, String roleName) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Role r = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        u.getRoles().add(r);
        userRepository.save(u);
    }

    @Override
    public void removeRoleFromUser(String userId, String roleName) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.get().getRoles().remove(role);
        userRepository.save(user.get());
    }

    @Override
    public void deleteById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        } else {
            user.get().setActive(false);
            user.get().getRoles().clear();
            userRepository.save(user.get());
        }
    }

    @Override
    @Transactional
    public void deleteUser(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        cartRepository.findByUserLogin(login).ifPresent(cart -> {
            cartItemRepository.deleteAllByCartId(cart.getId());
            cartRepository.delete(cart);
        });

        List<Order> orders = orderRepository.findAllByUserLogin(login);
        orderRepository.deleteAll(orders);

        userRepository.delete(user);
    }
}
