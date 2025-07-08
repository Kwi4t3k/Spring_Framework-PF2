package com.example.store.controller;

import com.example.store.model.Cart;
import com.example.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/view")
    public Cart viewCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.getCart(userDetails.getUsername());
    }

    @PostMapping("/add")
    public void addItem(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String bookId) {
        cartService.addToCart(userDetails.getUsername(), bookId);
    }

    @DeleteMapping("/delete/{itemId}")
    public void deleteItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String itemId) {
        cartService.removeFromCart(userDetails.getUsername(), itemId);
    }

    @DeleteMapping("/clear")
    public void clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
    }
}
