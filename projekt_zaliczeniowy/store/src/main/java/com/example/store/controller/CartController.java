package com.example.store.controller;

import com.example.store.model.CartItem;
import com.example.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/view")
    public ResponseEntity<List<CartItem>> viewCart(@AuthenticationPrincipal UserDetails userDetails) {
        List<CartItem> items = cartService.getCartItems(userDetails.getUsername());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItem(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String bookId) {
        cartService.addToCart(userDetails.getUsername(), bookId);
        return ResponseEntity.ok("Book " + bookId + " added to cart");
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String itemId) {
        CartItem cartItem = cartService.findCartItemById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item " + itemId + " not found"));

        String ownerLogin = cartItem.getCart().getUser().getLogin();

        if (!ownerLogin.equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to remove this item");
        }

        cartService.removeFromCart(userDetails.getUsername(), itemId);
        return ResponseEntity.ok("Item " + itemId + " removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok("Cart cleared");
    }
}
