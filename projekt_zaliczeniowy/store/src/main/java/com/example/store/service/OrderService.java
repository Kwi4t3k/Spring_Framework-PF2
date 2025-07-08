package com.example.store.service;

public interface OrderService {
    String createCheckoutSession(String orderId);

    void handleWebhook(String payload, String signature);
}
