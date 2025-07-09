package com.example.store.controller;

import com.example.store.model.OrderStatus;
import com.example.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

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

    @PostMapping("/changeStatus/{orderId}")
    public ResponseEntity<String> changeStatus(@PathVariable String orderId, @RequestParam OrderStatus orderStatus) {
        try {
            if (orderStatus.toString().isEmpty() || orderId == null) {
                return ResponseEntity.badRequest().body("Status cannot be empty");
            }

            EnumSet<OrderStatus> validStatuses = EnumSet.allOf(OrderStatus.class);
            if (!validStatuses.contains(orderStatus)) {
                return ResponseEntity
                        .badRequest()
                        .body("Wrong status: " + orderStatus);
            }

            orderService.changeStatus(orderId, orderStatus);
            return ResponseEntity.ok("Status " + orderStatus + " set to order " + orderId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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