package org.example.services;

import org.example.models.User;
import org.example.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String login, String rawPassword) {
        return userRepository.findByLogin(login).filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
    }

    public boolean register(String login, String rawPassword, String role) {
        if (userRepository.findByLogin(login).isPresent()) {
            System.out.println("Użytkownik o tym loginie już istnieje.");
            return false;
        }

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = User.builder()
                .login(login)
                .password(hashed)
                .role(role)
                .build();

        userRepository.save(user);
        System.out.println("Zarejestrowano pomyślnie.");
        return true;
    }

    public boolean isLoginTaken(String newLogin) {
        return userRepository.findByLogin(newLogin).isPresent();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}