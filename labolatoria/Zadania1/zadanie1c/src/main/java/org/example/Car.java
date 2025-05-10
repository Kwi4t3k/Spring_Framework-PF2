package org.example;

public class Car extends Vehicle {
    public Car(String brand, String model, int year, float price, String id) {
        super(brand, model, year, price, id);
    }

    @Override
    public String toCSV() {
        return getBrand() + ";" + getModel() + ";" + getYear() + ";" + getPrice() + ";" + getId() + ";" + isRented();
    }

    @Override
    public Vehicle clone() {
        return new Car(getBrand(), getModel(), getYear(), getPrice(), getId());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}