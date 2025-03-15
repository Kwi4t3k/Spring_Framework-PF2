package org.example;

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
}