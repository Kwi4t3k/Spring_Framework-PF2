package com.example.store.repository;

import com.example.store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByStripeSessionId(String stripeSessionId);

    List<Order> findAllByUserLogin(String login);
}
