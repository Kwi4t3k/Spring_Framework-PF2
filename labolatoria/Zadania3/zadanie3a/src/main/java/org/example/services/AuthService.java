package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String password) {
        return userRepository.findByLogin(login).filter(user -> BCrypt.checkpw(password, user.getPassword()));
    }

    public User register(String login, String password, String role) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
                .login(login)
                .password(password)
                .role(role)
                .build();
        return userRepository.save(user);
    }
}