package com.example.store.service;

public interface PaymentService {
    String createCheckoutSession(String rentalId);

    void handleWebhook(String payload, String signature);
}
