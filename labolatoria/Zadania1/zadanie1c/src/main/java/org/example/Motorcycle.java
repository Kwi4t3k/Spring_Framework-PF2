package org.example;

import java.util.Objects;

public class Motorcycle extends Vehicle {
    private final String category; // kategoria prawa jazdy (AM, A1, A2, A)

    public Motorcycle(String brand, String model, int year, float price, String id, String category) {
        super(brand, model, year, price, id);
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toCSV() {
        return getBrand() + ";" + getModel() + ";" + getYear() + ";" + getPrice() + ";" + getId() + ";" + isRented() + ";" + category;
    }

    @Override
    public Vehicle clone() {
        return new Motorcycle(getBrand(), getModel(), getYear(), getPrice(), getId(), category);
    }

    @Override
    public String toString() {
        return super.toString().replace("}", ", category='" + category + "'}");
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        Motorcycle motorcycle = (Motorcycle) obj;
        return Objects.equals(category, motorcycle.category);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(category);
    }
}