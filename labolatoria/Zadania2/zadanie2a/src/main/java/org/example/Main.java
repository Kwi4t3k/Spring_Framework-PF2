package org.example;

//import org.apache.commons.codec.digest.DigestUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepository = new VehicleRepository();
        IUserRepository userRepository = new UserRepository();
        Authentication authentication = new Authentication(userRepository);
        Scanner scanner = new Scanner(System.in);

        System.out.println("** Wypożyczalnia pojazdów **");
        System.out.println("Podaj login:");
        String login = scanner.nextLine();
        System.out.println("Podaj hasło:");
        String password = scanner.nextLine();

        User currentUser = authentication.authenticate(login, password);
        if (currentUser == null) {
            System.out.println("Błędne logowanie");
            return;
        }

        boolean isAdmin = currentUser.getRole().equals("admin");

        while (true) {
            System.out.println("\n** Menu **");
            System.out.println("1 - Wyświetl dostępne pojazdy");
            System.out.println("2 - Wyświetl wypożyczone pojazdy");
            System.out.println("3 - Wypożycz pojazd");
            System.out.println("4 - Zwróć pojazd");
            System.out.println("5 - Wyświetl swoje dane");

            if (isAdmin) {
                System.out.println("6 - Dodaj pojazd");
                System.out.println("7 - Usuń pojazd");
                System.out.println("8 - Wyświetl listę użykowników");
            }

            System.out.println("0 - Wyjście\n");
            System.out.println("Wybierz opcję:");

            int wybor = scanner.nextInt();
            scanner.nextLine();

            switch (wybor) {
                case 0:
                    System.out.println("Do widzenia");
                    return;
                case 1:
                    System.out.println("Dostępne pojazdy:");
                    boolean availableVehicles = false;
                    for (Vehicle vehicle : vehicleRepository.getVehicles()) {
                        if (!vehicle.isRented()) {
                            System.out.println(vehicle);
                            availableVehicles = true;
                        }
                    }
                    if (!availableVehicles) {
                        System.out.println("Brak dostępnych pojazdów");
                    }
                    break;
                case 2:
                    System.out.println("Wypożyczone pojazdy:");
                    boolean rentedVehicles = false;
                    for (Vehicle vehicle : vehicleRepository.getVehicles()) {
                        if (vehicle.isRented()) {
                            System.out.println(vehicle);
                            rentedVehicles = true;
                        }
                    }
                    if (!rentedVehicles) {
                        System.out.println("Brak wypożyczonych pojazdów");
                    }
                    break;
                case 3:
                    if (currentUser.getRentedVehicleId() == null) {
                        System.out.println("Podaj id pojazdu do wypożyczenia:");
                        String id = scanner.nextLine();
                        Vehicle vehicle = vehicleRepository.getVehiclebyId(id);
                        if (vehicle != null && !vehicle.isRented()) {
                            vehicleRepository.rentVehicle(id);
                            currentUser.rentVehicle(id);
                            userRepository.save();
                            vehicleRepository.save();
                            System.out.println("Pojazd został wypożyczony");
                        } else {
                            System.out.println("Pojazd nie jest dostępny");
                        }
                    } else {
                        System.out.println("Pojazd już jest wypożyczony przez ciebie");
                    }
                    break;
                case 4:
                    if (currentUser.getRentedVehicleId() != null) {
                        String id = currentUser.getRentedVehicleId();
                        Vehicle vehicle = vehicleRepository.getVehiclebyId(id);
                        if (vehicle != null && vehicle.isRented()) {
                            vehicleRepository.returnVehicle(id);
                            currentUser.returnVehicle();
                            userRepository.save();
                            vehicleRepository.save();
                            System.out.println("Pojazd został zwrócony");
                        } else {
                            System.out.println("Pojazd nie został zwrócony");
                        }
                    } else {
                        System.out.println("Nie masz wypożyczonego pojazdu");
                    }
                    break;
                case 5:
                    System.out.println("Nazywasz się: " + currentUser.getLogin() + " | jesteś na poziomie: " + currentUser.getRole());
                    break;
                case 6:
                    if (isAdmin) {
                        System.out.println("Podaj typ pojazdu: (Car/Motorcycle)");
                        String type = scanner.nextLine();

                        System.out.println("Podaj markę pojazdu:");
                        String brand = scanner.nextLine();

                        System.out.println("Podaj model pojazdu:");
                        String model = scanner.nextLine();

                        System.out.println("Podaj rocznik pojazdu:");
                        int year = Integer.parseInt(scanner.nextLine());

                        System.out.println("Podaj cenę pojazdu:");
                        float price = Float.parseFloat(scanner.nextLine());

                        scanner.nextLine();

                        Vehicle newVehicle;
                        if (type.equalsIgnoreCase("Car")) {
                            String id = vehicleRepository.generateNextID();
                            newVehicle = new Car(brand, model, year, price, id);
                        } else if (type.equalsIgnoreCase("Motorcycle")) {
                            System.out.println("Podaj kategorię pojazdu: (AM, A1, A2, A)");
                            String category = scanner.nextLine();

                            String id = vehicleRepository.generateNextID();
                            newVehicle = new Motorcycle(brand, model, year, price, id, category);
                        }
                    }
                case 8:
                    if (isAdmin) {
                        for (User user : userRepository.getUsers()) {
                            System.out.println(user);
                        }
                    }
                    break;
            }
        }
    }
}