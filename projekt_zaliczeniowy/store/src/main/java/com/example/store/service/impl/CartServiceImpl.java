package com.example.store.service.impl;

import com.example.store.model.Book;
import com.example.store.model.Cart;
import com.example.store.model.CartItem;
import com.example.store.repository.BookRepository;
import com.example.store.repository.CartItemRepository;
import com.example.store.repository.CartRepository;
import com.example.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public Cart getCart(String login) {
        return cartRepository.findByUserLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("No cart for user: " + login));
    }

    @Override
    public void addToCart(String login, String bookId) {
        Cart cart = getCart(login);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with ID: " + bookId + " not found"));
        if (!book.isActive()) {
            throw new IllegalStateException("Book is not available");
        }

        boolean alreadyInCart = cartItemRepository.existsByCartIdAndBookId(cart.getId(), bookId);
        if (alreadyInCart) {
            throw new IllegalArgumentException("Book with ID: " + bookId + " is already in your cart");
        }

        CartItem cartItem = CartItem.builder()
                .id(UUID.randomUUID().toString())
                .cart(cart)
                .book(book)
                .build();

//        cart.getCartItems().add(cartItem);
//        cartRepository.save(cart);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(String login, String cartItemId) {
//        Cart cart = getCart(login);
//        cart.getCartItems().removeIf(cartItem -> cartItem.getId().equals(cartItemId));
//        cartRepository.save(cart);
        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void clearCart(String login) {
        Cart cart = getCart(login);
//        cart.getCartItems().clear();
//        cartRepository.save(cart);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(String login) {
        Cart cart = cartRepository.findByUserLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("No cart for user: " + login));
        return cartItemRepository.findAllByCartId(cart.getId());
    }

    @Override
    public Optional<CartItem> findCartItemById(String itemId) {
        return cartItemRepository.findById(itemId);
    }
}
