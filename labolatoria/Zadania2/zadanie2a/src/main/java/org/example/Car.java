package org.example;

public class Car extends Vehicle {
    public Car(String brand, String model, int year, float price, String id) {
        super(brand, model, year, price, id);
    }

    @Override
    public String toCSV() {
        return brand + ";" + model + ";" + year + ";" + price + ";" + id + ";" + rented;
    }
}