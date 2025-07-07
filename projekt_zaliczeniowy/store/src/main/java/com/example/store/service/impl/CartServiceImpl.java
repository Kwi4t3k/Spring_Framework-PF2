package com.example.store.service.impl;

import com.example.store.model.Book;
import com.example.store.model.Cart;
import com.example.store.model.CartItem;
import com.example.store.repository.BookRepository;
import com.example.store.repository.CartRepository;
import com.example.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    @Override
    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No cart for user: " + userId));
    }

    @Override
    public void addToCart(String userId, String bookId, int quantity) {
        Cart cart = getCart(userId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with ID: " + bookId + " not found"));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .book(book)
                .quantity(quantity)
                .build();

        cart.getCartItems().add(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(String userId, UUID cartItemId) {
        Cart cart = getCart(userId);
        cart.getCartItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String userId) {
        Cart cart = getCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
