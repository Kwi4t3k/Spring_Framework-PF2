package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository implements IVehicleRepository {
    private final List<Vehicle> vehicles;
    private final String pathToFile = "src/main/resources/vehicles.txt";
    private int nextCarId = 1;
    private int nextMotoId = 1;

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
        System.err.println("Pojazd o id: " + id + " został już wypożyczony lub nie istnieje");
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
        System.err.println("Pojazd o id: " + id + "nie został zwrócony");
    }

    @Override
    public List<Vehicle> getVehicles() {
        return new ArrayList<>(vehicles);
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
                if (parts.length == 6) { // Samochód
                    String brand = parts[0];
                    String model = parts[1];
                    int year = Integer.parseInt(parts[2]);
                    float price = Float.parseFloat(parts[3]);
                    String id = parts[4];
                    boolean rented = Boolean.parseBoolean(parts[5]);

                    Car car = new Car(brand, model, year, price, id);
                    car.setRented(rented);
                    vehicles.add(car);

                    if (id.startsWith("CAR_")) {
                        int num = Integer.parseInt(id.substring(4)); // CAR_001 -> 1
                        if (num >= nextCarId) {
                            nextCarId = num + 1;
                        }
                    }
                } else if (parts.length == 7) { // Motocykl
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

                    if (id.startsWith("MOTO_")) {
                        int num = Integer.parseInt(id.substring(5)); // MOTO_001 -> 1
                        if (num >= nextMotoId) {
                            nextMotoId = num + 1;
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Błąd przy odczycie pliku " + e.getMessage());
        }
    }

    @Override
    public void addVehicle(Vehicle vehicle) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicle.getId())) {
                System.err.println("Pojazd o id: " + vehicle.getId() + " już intnieje w pamięci");
                return;
            }
        }

        if (ifIdExistsInFile(vehicle.getId())) {
            System.err.println("Pojazd o id: " + vehicle.getId() + " już istnieje w pliku");
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

    @Override
    public void removeVehicle(String id) {
        Vehicle vehicleToRemove = getVehiclebyId(id);

        if (vehicleToRemove != null && !vehicleToRemove.isRented()) {
            boolean removed = vehicles.removeIf(vehicle -> vehicle.getId().equals(id));

            if (removed) {
                save();
                System.out.println("Usunięto pojazd o id: " + id);
            } else {
                System.err.println("Nie usunięto pojazdu o id: " + id);
            }
        } else {
            System.err.println("Nie możesz usunąć wypożyczonego pojazdu");
        }
    }

    @Override
    public Vehicle getVehiclebyId(String id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }

    @Override
    public String generateNextCarId() {
        return String.format("CAR_%03d", nextCarId++);
    }

    @Override
    public String generateNextMotoId() {
        return String.format("MOTO_%03d", nextMotoId++);
    }
}