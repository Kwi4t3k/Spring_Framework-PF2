package com.example.store.service;

import com.example.store.model.Cart;

import java.util.UUID;

public interface CartService {
    Cart getCart(String userId);

    void addToCart(String userId, String bookId, int quantity);

    void removeFromCart(String userId, UUID cartItemId);

    void clearCart(String userId);
}
