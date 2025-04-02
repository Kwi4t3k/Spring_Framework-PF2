package org.example.models;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Vehicle {
    private String id;
    private String category;
    private String brand;
    private String model;
    private int year;
    private String plate;
    private double price;

    @Builder.Default
    private Map<String, Object> attributes = Map.of();

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }
}