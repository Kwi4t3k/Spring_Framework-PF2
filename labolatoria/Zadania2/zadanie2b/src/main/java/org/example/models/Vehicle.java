package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Vehicle {
    private String brand, model;
    private int year;
    private float price;
    private boolean rented;
    private String id;

//    public Vehicle(String brand, String model, int year, float price, String id) {
//        this.brand = brand;
//        this.model = model;
//        this.year = year;
//        this.price = price;
//        this.rented = false;
//        this.id = id;
//    }

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