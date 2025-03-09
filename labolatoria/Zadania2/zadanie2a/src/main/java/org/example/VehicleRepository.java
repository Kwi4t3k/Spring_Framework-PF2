package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository implements IVehicleRepository {
    private final List<Vehicle> vehicles;
    private final String pathToFile = "src/main/resources/vehicles.txt";

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
        return List.copyOf(vehicles);
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

    public boolean ifIdExistsInFile(String id) {
        File file = new File(pathToFile);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 5 && parts[4].equals(id)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            return false;
        }
        return false;
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        if (ifIdExistsInFile(vehicle.id)) {
            System.out.println("Pojazd o id: " + vehicle.id + " istnieje w pliku");
            return;
        }

        vehicles.add(vehicle);
        save();
        System.out.println("Dodano pojazd: " + vehicle);
    }

    @Override
    public void removeVehicle(String id) {
        if (vehicles.removeIf(vehicle -> vehicle.id.equals(id))) {
            save();
            System.out.println("Usunięto pojazd o id: " + id);
        } else {
            System.out.println("Nie usunięto pojazdu o id: " + id);
        }
    }
}