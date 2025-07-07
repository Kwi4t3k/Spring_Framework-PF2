package com.example.store.repository;

import com.example.store.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
    Optional<Rental> findByBookIdAndReturnDateIsNull(String bookId);

    List<Rental> findByUserIdAndReturnDateIsNull(String userId);
}