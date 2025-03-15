package org.example;

import java.util.Objects;

public abstract class Vehicle {
    private final String brand, model;
    private final int year;
    private final float price;
    private boolean rented;
    private final String id;

    public Vehicle(String brand, String model, int year, float price, String id) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = false;
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public float getPrice() {
        return price;
    }

    public boolean isRented() {
        return rented;
    }

    public String getId() {
        return id;
    }

    protected void setRented(boolean rented) {
        this.rented = rented;
    }

    public abstract String toCSV();
    public abstract Vehicle clone();

    @Override
    public String toString() {
        return "Vehicle{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", price=" + price +
                ", rented=" + rented +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vehicle vehicle = (Vehicle) obj;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}