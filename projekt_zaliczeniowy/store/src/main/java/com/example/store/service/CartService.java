package com.example.store.service;

import com.example.store.model.Cart;
import com.example.store.model.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartService {
    Cart getCart(String userId);

    void addToCart(String userId, String bookId);

    void removeFromCart(String userId, String cartItemId);

    void clearCart(String userId);

    List<CartItem> getCartItems(String login);

    Optional<CartItem> findCartItemById(String itemId);
}
