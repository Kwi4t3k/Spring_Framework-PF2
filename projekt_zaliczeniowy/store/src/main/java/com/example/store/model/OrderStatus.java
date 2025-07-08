package com.example.store.model;


public enum OrderStatus {
    NEW,            // nowe zamówienie (jeszcze nie złożone)
    PENDING,        // oczekujące na płatność
    PAID,           // opłacone
    SHIPPED,        // wysłane
    DELIVERED,      // doręczone
}