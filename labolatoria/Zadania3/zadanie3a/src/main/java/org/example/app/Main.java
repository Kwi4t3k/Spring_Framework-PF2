package org.example.app;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.repositories.RentalRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;
import org.example.repositories.impl.VehicleJsonRepository;
import org.example.services.AuthService;
import org.example.services.RentalService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserJsonRepository();
        VehicleRepository vehicleRepository = new VehicleJsonRepository();
        RentalRepository rentalRepository = new RentalJsonRepository();

        AuthService authService = new AuthService(userRepository);
        RentalService rentalService = new RentalService(rentalRepository, userRepository, vehicleRepository);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("** Wypożyczalnia pojazdów **");
            System.out.println("Podaj login:");
            String login = scanner.nextLine();
            System.out.println("Podaj hasło:");
            String password = scanner.nextLine();

            Optional<User> userOpt = authService.login(login, password);

            if (userOpt.isEmpty()) {
                System.err.println("Błędne dane logowania");
                continue;
            }

            User currentUser = userOpt.get();
            boolean isAdmin = currentUser.getRole().equals("ADMIN");
            boolean loggedIn = true;

            System.out.println("Witaj, " + currentUser.getLogin() + "!");

            while (loggedIn) {
                System.out.println("\n** Menu **");
                if (isAdmin) {
                    System.out.println("-1 - Zakończ program");
                }
                System.out.println("0 - Wyloguj");
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
                System.out.println("Wybierz opcję:");

                int choice = scanner.nextInt();

                switch (choice) {
                    case -1:
                        if (isAdmin) {
                            System.out.println("Do widzenia");
                            running = false;
                            loggedIn = false;
                        } else {
                            System.err.println("Brak uprawnień");
                        }
                        break;
                    case 0:
                        loggedIn = false;
                        System.out.println("Wylogowano: " + currentUser.getLogin());
                        break;
                    case 1:
                        System.out.println("Dostępne pojazdy:");

                        List<Vehicle> availableVehicles = vehicleRepository.findAll().stream().filter(vehicle -> !vehicle.isRented()).toList();

                        if (availableVehicles.isEmpty()) {
                            System.out.println("Brak dostępnych pojazdów");
                        } else {
                            for (Vehicle vehicle : availableVehicles) {
                                System.out.println(vehicle);
                            }
                        }
                        break;
                    case 2:
                        System.out.println("Wypożyczone pojazdy:");

                        List<Vehicle> rentedVehicles = vehicleRepository.findAll().stream().filter(vehicle -> vehicle.isRented()).toList();

                        if (rentedVehicles.isEmpty()) {
                            System.out.println("Brak wypożyczonych pojazdów");
                        } else {
                            for (Vehicle vehicle : rentedVehicles) {
                                System.out.println(vehicle);
                            }
                        }
                        break;
                    case 3:
                        if (rentalRepository.findByUserId(currentUser.getId()).isEmpty()) {
                            System.out.println("Podaj id pojazdu do wypożyczenia:");
                            String rentId = scanner.nextLine();

                            Optional<Vehicle> vehicleToRentOpt = vehicleRepository.findById(rentId);

                            if (vehicleToRentOpt.isEmpty()) {
                                System.err.println("Nie znaleziono pojazdu o podanym id");
                            } else {
                                Vehicle vehicleToRent = vehicleToRentOpt.get();

                                if (vehicleToRent.isRented()) {
                                    System.err.println("Ten pojazd jest już wypożyczony przez innego użtkownika");
                                } else {
                                    boolean rentalSucces = rentalService.rentVehicle(currentUser, vehicleToRent);

                                    if (rentalSucces) {
                                        System.out.println("Pojazd o id: " + rentId + ", został wypożyczony");
                                    } else {
                                        System.err.println("Pojazd o id: " + rentId + ", nie został wypożyczony");
                                    }
                                }
                            }
                        } else {
                            System.err.println("Pojazd o id: " + rentalRepository.findByUserId(currentUser.getId()).get().getVehicleId() + " jest już wypożyczony przez ciebie");
                        }
                        break;
                    case 4:
                        Optional<Rental> userRentalOpt = rentalRepository.findByUserId(currentUser.getId());

                        if (userRentalOpt.isPresent()) {
                            Rental userRental = userRentalOpt.get();
                            String rentedVehicleId = userRental.getVehicleId();

                            Optional<Vehicle> rentedVehicleOpt = vehicleRepository.findById(rentedVehicleId);

                            if (rentedVehicleOpt.isEmpty()) {
                                System.err.println("Nie znaleziono pojazdu o id: " + rentedVehicleId);
                            } else {
                                Vehicle rentedVehicle = rentedVehicleOpt.get();

                                boolean returnSuccess = rentalService.returnVehicle(currentUser);

                                if (returnSuccess) {
                                    System.out.println("Pojazd o id: " + rentedVehicle.getId() + " został zwrócony.");
                                } else {
                                    System.err.println("Nie udało się zwrócić pojazdu");
                                }
                            }
                        } else {
                            System.out.println("Aktualnie nie masz wypożyczonego żadnego pojazdu.");
                        }
                        break;
                    case 5:
                        System.out.println("Nazywasz się: " + currentUser.getLogin() + " | twoja rola to: " + currentUser.getRole());

                        if (rentalService.getUserRental(currentUser).isPresent()) {
                            System.out.println("Wypożyczony pojazd: " + rentalService.getUserRental(currentUser).get().getVehicleId());
                        } else {
                            System.err.println("Brak wypożyczonego pojazdu");
                        }
                        break;
                    case 6:
                        if (isAdmin) {
                            System.out.println("Podaj typ pojazdu (Car/Motorcycle/Bus):");
                            String category = scanner.nextLine();

                            System.out.print("Numer rejestracyjny:");
                            String plate = scanner.nextLine();

                            System.out.println("Podaj markę pojazdu:");
                            String brand = scanner.nextLine();

                            System.out.println("Podaj model pojazdu:");
                            String model = scanner.nextLine();

                            System.out.println("Podaj rok produkcji:");
                            int year = Integer.parseInt(scanner.nextLine());

                            Map<String, Object> attributes = new HashMap<>();

                            if (category.equalsIgnoreCase("Bus")) {
                                System.out.print("Liczba miejsc: ");
                                int seats = Integer.parseInt(scanner.nextLine());
                                attributes.put("seats", seats);
                            } else if (category.equalsIgnoreCase("Motorcycle")) {
                                System.out.print("Kategoria prawa jazdy (AM/A1/A2/A): ");
                                String licenceCategory = scanner.nextLine();
                                System.out.print("Rodzaj napędu (chain/belt/shaft): ");
                                String drive = scanner.nextLine();
                                attributes.put("licence_category", licenceCategory);
                                attributes.put("drive", drive);
                            }

                            String id = UUID.randomUUID().toString();

                            Vehicle newVehicle = Vehicle.builder()
                                    .id(id)
                                    .category(category)
                                    .plate(plate)
                                    .brand(brand)
                                    .model(model)
                                    .year(year)
                                    .rented(false)
                                    .attributes(attributes)
                                    .build();

                            vehicleRepository.save(newVehicle);
                            System.out.println("Dodano nowy pojazd: " + newVehicle);
                        } else {
                            System.err.println("Brak uprawnień");
                        }
                        break;
                    case 7:
                        if (isAdmin) {
                            System.out.println("Podaj id pojazdu do usunięcia:");
                            String id = scanner.nextLine();
                            vehicleRepository.deleteById(id);
                            System.out.println("Pojazd " + vehicleRepository.findById(id) + " został usunięty");
                        } else {
                            System.err.println("Brak uprawnień");
                        }
                        break;
                    case 8:
                        if (isAdmin) {
                            for (User user : userRepository.getUsers()) {
                                System.out.println(user);
                            }
                        } else {
                            System.err.println("Brak uprawnień");
                        }
                        break;
                    default:
                        System.err.println("Nie ma takiej opcji");
                        break;
                }
            }
        }
    }
}