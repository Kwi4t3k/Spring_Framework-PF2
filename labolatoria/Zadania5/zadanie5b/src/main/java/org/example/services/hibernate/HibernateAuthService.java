package org.example.services.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.impl.hibernate.UserHibernateRepository;
import org.example.services.AuthService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class HibernateAuthService implements AuthService {
    private final UserHibernateRepository userRepository;

    public HibernateAuthService(UserHibernateRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return userRepository.findByLogin(login)
                    .filter(user -> BCrypt.checkpw(rawPassword, user.getPassword()));
        }
    }

    @Override
    public boolean register(String login, String rawPassword, String role) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            userRepository.setSession(session);

            if (userRepository.findByLogin(login).isPresent()) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            User user = User.builder()
                    .login(login)
                    .password(hashedPassword)
                    .role(role)
                    .build();

            userRepository.save(user);
            tx.commit();
            return true;
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            return userRepository.findAll();
        }
    }
}
