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
            if (vehicle.getId().equals(id) && !vehicle.isRented()) {
                vehicle.setRented(true);
                save();
                System.out.println("Wypożyczony pojazd: " + vehicle);
                return;
            }
        }
        System.out.println("Pojazd o id: " + id + " został już wypożyczony lub nie istnieje");
    }

    @Override
    public void returnVehicle(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id) && vehicle.isRented()) {
                vehicle.setRented(false);
                save();
                System.out.println("Zrwócono pojazd: " + vehicle);
                return;
            }
        }
        System.out.println("Pojazd o id: " + id + " został zwrócony");
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> clonedVehicles = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            clonedVehicles.add(vehicle.clone());
        }
        return List.copyOf(clonedVehicles);
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
        if (!file.exists()) {
            return;
        }

        vehicles.clear();

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
                    car.setRented(rented);
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
                    motorcycle.setRented(rented);
                    vehicles.add(motorcycle);
                }
            }

        } catch (IOException e) {
            System.err.println("Błąd przy odczycie pliku " + e.getMessage());
        }
    }

    @Override // dodatkowe do testów
    public void addVehicle(Vehicle vehicle) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicle.getId())) {
                System.out.println("Pojazd o id: " + vehicle.getId() + " już intnieje w pamięci");
                return;
            }
        }

        if (ifIdExistsInFile(vehicle.getId())) {
            System.out.println("Pojazd o id: " + vehicle.getId() + " już istnieje w pliku");
            return;
        }

        vehicles.add(vehicle);
        save();
        System.out.println("Dodano pojazd: " + vehicle);
    }

    private boolean ifIdExistsInFile(String id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 5 && parts[4].equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd przy odczycie pliku " + e.getMessage());
        }
        return false;
    }
}