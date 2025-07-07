package com.example.store.service;

import com.example.store.model.Rental;

import java.util.List;

public interface RentalService {
    List<Rental> getUserActiveRentals(String userId);

    Rental rent(String bookId, String userId);

    Rental returnRental(String bookId, String userId);

    List<Rental> findByUserId(String userId);
}
