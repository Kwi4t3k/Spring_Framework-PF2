package com.example.zadanie8.repository;

import com.example.zadanie8.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLogin(String login);
    Optional<User> findByLoginAndIsActiveTrue(String login);
    Optional<User> findByIdAndIsActiveTrue(String id);
}