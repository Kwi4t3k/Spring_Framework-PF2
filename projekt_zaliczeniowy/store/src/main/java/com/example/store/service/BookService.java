package com.example.store.service;

import com.example.store.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll();

    List<Book> findAllAvailableBooks();

    boolean isAvailable(String bookId);

    Optional<Book> findById(String bookId);

    Book save(Book book);

    void deleteById(String bookId);

    void eraseById(String bookId);
}
