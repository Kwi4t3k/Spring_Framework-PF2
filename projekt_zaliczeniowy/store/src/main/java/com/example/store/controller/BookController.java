package com.example.store.controller;

import com.example.store.model.Book;
import com.example.store.service.BookService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    Logger logger = LoggerFactory.getLogger(BookController.class);

    @GetMapping("/allBooks")
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/allAvailableBooks")
    public List<Book> getAllAvailableBooks() {
        return bookService.findAllAvailableBooks();
    }

    @GetMapping("/isBookAvailable/{bookId}")
    public boolean isBookAvailable(@PathVariable("bookId") String bookId) {
        return bookService.isAvailable(bookId);
    }

    @GetMapping("/findBookById/{bookId}")
    public ResponseEntity<Book> findBookById(@PathVariable("bookId") String bookId) {
        return bookService.findById(bookId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.error("Book not found: {}", bookId);
                    return ResponseEntity.notFound().build();
                });
    }

    // dodawanie i usuwanie książek
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.save(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while saving book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable("bookId") String bookId) {
        try {
            bookService.deleteById(bookId);
            return ResponseEntity.ok("Book with ID: " + bookId + " deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/erase/{bookId}")
    public ResponseEntity<String> eraseBook(@PathVariable("bookId") String bookId) {
        try {
            bookService.eraseById(bookId);
            return ResponseEntity.ok("Book with ID: " + bookId + " permanently deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to erase book: " + e.getMessage());
        }
    }
}