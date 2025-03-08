package org.example;

import java.util.Objects;

public abstract class Vehicle {
    protected String brand, model;
    protected int year;
    protected float price;
    protected boolean rented;
    protected String id;

    public Vehicle(String brand, String model, int year, float price, String id) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = false;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract String toCSV();

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