package com.example.zadanie9.service;

public interface PaymentService {
    String createCheckoutSession(String rentalId);
    void handleWebhook(String payload, String signature);
}
