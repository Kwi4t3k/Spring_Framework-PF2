package com.example.store.service;

import com.example.store.model.OrderStatus;

public interface OrderService {
    String createCheckoutSession(String orderId);

    void handleWebhook(String payload, String signature);

    String placeOrder(String username);

    void changeStatus(String orderId, OrderStatus orderStatus);
}
