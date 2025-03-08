package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository implements IVehicleRepository {
    private List<Vehicle> vehicles;
    private String pathToFile = "src/main/resources/vehicles.txt";

    public VehicleRepository() {
        this.vehicles = new ArrayList<>();
        load();
    }

    @Override
    public void rentVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.id.equals(id) && !vehicle.rented) {
                System.out.println("Wypożyczony pojazd: " + vehicle);
                vehicle.rented = true;
                save();
                return;
            }
        }
        System.out.println("Pojazd o id: " + id + " został już wypożyczony lub nie istnieje");
    }

    @Override
    public void returnVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.id.equals(id) && vehicle.rented) {
                System.out.println("Zrwócono pojazd: " + vehicle);
                vehicle.rented = false;
                save();
                return;
            }
        }
        System.out.println("Pojazd o id: " + id + " został zwrócony");
    }

    @Override
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            for (Vehicle vehicle : vehicles) {
                writer.write(vehicle.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Plik sie nie zapisal " + e.getMessage());
        }
    }

    @Override
    public void load() {
        File file = new File(pathToFile);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Plik sie zrobił");
                }
            }
        } catch (IOException e) {
            System.err.println("Plik sie nie zrobił");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    String brand = parts[0];
                    String model = parts[1];
                    int year = Integer.parseInt(parts[2]);
                    float price = Float.parseFloat(parts[3]);
                    String id = parts[4];
                    boolean rented = Boolean.parseBoolean(parts[5]);

                    Car car = new Car(brand, model, year, price, id);
                    car.rented = rented;
                    vehicles.add(car);
                } else if (parts.length == 7) {
                    String brand = parts[0];
                    String model = parts[1];
                    int year = Integer.parseInt(parts[2]);
                    float price = Float.parseFloat(parts[3]);
                    String id = parts[4];
                    boolean rented = Boolean.parseBoolean(parts[5]);
                    String category = parts[6];

                    Motorcycle motorcycle = new Motorcycle(brand, model, year, price, id, category);
                    motorcycle.rented = rented;
                    vehicles.add(motorcycle);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}