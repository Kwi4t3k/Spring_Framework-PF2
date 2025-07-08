package com.example.store.controller;

import com.example.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<String> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        String orderId = orderService.placeOrder(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<String> createCheckoutSession(@PathVariable String orderId) {
        String url = orderService.createCheckoutSession(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        orderService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("succes");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        return ResponseEntity.ok("cancel");
    }
}