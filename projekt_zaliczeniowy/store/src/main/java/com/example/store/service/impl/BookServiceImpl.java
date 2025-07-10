package com.example.store.service.impl;

import com.example.store.model.Book;
import com.example.store.model.CartItem;
import com.example.store.model.Order;
import com.example.store.repository.BookRepository;
import com.example.store.repository.CartItemRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findAllAvailableBooks() {
        return bookRepository.findByIsActiveTrue();
    }

    @Override
    public boolean isAvailable(String bookId) {
        return bookRepository.findById(bookId)
                .filter(Book::isActive)
                .isPresent();
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return bookRepository.findById(bookId);
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId().isEmpty()) {
            book.setId(UUID.randomUUID().toString());
            book.setActive(true);
        }
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(String bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found by id: " + bookId));
        book.setActive(false);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void eraseById(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found by id: " + bookId));

        List<CartItem> cartItems = cartItemRepository.findAll()
                .stream()
                .filter(ci -> ci.getBook().getId().equals(bookId))
                .toList();
        cartItemRepository.deleteAll(cartItems);

        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.getItems().removeIf(oi -> oi.getBook().getId().equals(bookId));
        }
        orderRepository.saveAll(orders);

        bookRepository.delete(book);
    }
}
