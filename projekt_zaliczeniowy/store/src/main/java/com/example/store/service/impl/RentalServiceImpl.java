package com.example.store.service.impl;

import com.example.store.model.Book;
import com.example.store.model.Rental;
import com.example.store.model.User;
import com.example.store.repository.BookRepository;
import com.example.store.repository.RentalRepository;
import com.example.store.repository.UserRepository;
import com.example.store.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rental> getUserActiveRentals(String userId) {
        return findByUserId(userId).stream().filter(rental -> rental.getReturnDate() == null).toList();
    }

    @Override
    public List<Rental> findByUserId(String userId) {
        return rentalRepository.findAll().stream().filter(r -> r.getUser().getId().equals(userId)).toList();
    }

    @Override
    public Rental rent(String bookId, String userId) {
        if(rentalRepository.findByBookIdAndReturnDateIsNull(bookId).isPresent()) {
            throw new IllegalStateException("Book " + bookId + " is not available for rent.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with ID :" + bookId + "not found: "));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID :" + userId + "not found: "));

        Rental newRental = Rental.builder()
                .id(UUID.randomUUID().toString())
                .book(book)
                .user(user)
                .rentDate(LocalDateTime.now())
                .returnDate(null)
                .build();

        return rentalRepository.save(newRental);
    }

    @Override
    public Rental returnRental(String bookId, String userId) {
        Rental rental = rentalRepository.findByBookIdAndReturnDateIsNull(bookId)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Rental not found."));

        rental.setReturnDate(LocalDateTime.now());

        return rentalRepository.save(rental);
    }
}