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
            System.out.print("Podaj login: ");
            String login = scanner.nextLine();
            System.out.print("Podaj hasło: ");
            String password = scanner.nextLine();

            Optional<User> userOpt = authService.login(login, password);

            if (userOpt.isEmpty()) {
                System.err.println("Błędne dane logowania.");
                System.out.println("1 - Wpisz login ponownie");
                System.out.println("2 - Zarejestruj się");
                System.out.print("Wybierz opcję: ");
                String retryOption = scanner.nextLine();

                if (retryOption.equals("2")) {
                    System.out.println("** Rejestracja **");

                    System.out.print("Wprowadź nowy login: ");
                    String newLogin = scanner.nextLine();

                    if (userRepository.findByLogin(newLogin).isPresent()) {
                        System.err.println("Taki login już istnieje. Spróbuj się zalogować.");
                        continue;
                    }

                    System.out.print("Wprowadź hasło: ");
                    String newPassword = scanner.nextLine();

                    System.out.print("Podaj rolę (ADMIN / USER): ");
                    String role = scanner.nextLine().toUpperCase();

                    if (!role.equals("ADMIN") && !role.equals("USER")) {
                        System.err.println("Niepoprawna rola. Ustawiono domyślną: USER.");
                        role = "USER";
                    }

                    User newUser = authService.register(newLogin, newPassword, role);
                    System.out.println("Użytkownik zarejestrowany pomyślnie. Zaloguj się teraz.");
                    continue;
                } else {
                    System.out.println("Spróbuj ponownie.");
                    continue;
                }
            }

            User currentUser = userOpt.get();
            boolean isAdmin = currentUser.getRole().equalsIgnoreCase("ADMIN");
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
                    System.out.println("8 - Wyświetl listę użytkowników");
                }
                System.out.print("Wybierz opcję: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case -1:
                        if (isAdmin) {
                            running = false;
                            loggedIn = false;
                            System.out.println("Do widzenia");
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
                        List<Vehicle> available = vehicleRepository.findAll().stream().filter(v -> !v.isRented()).toList();
                        if (available.isEmpty()) {
                            System.out.println("Brak dostępnych pojazdów");
                        } else {
                            available.forEach(System.out::println);
                        }
                        break;
                    case 2:
                        System.out.println("Wypożyczone pojazdy:");
                        List<Vehicle> rented = vehicleRepository.findAll().stream().filter(Vehicle::isRented).toList();
                        if (rented.isEmpty()) {
                            System.out.println("Brak wypożyczonych pojazdów");
                        } else {
                            rented.forEach(System.out::println);
                        }
                        break;
                    case 3:
                        if (rentalRepository.findByUserId(currentUser.getId()).isEmpty()) {
                            System.out.print("Podaj ID pojazdu do wypożyczenia: ");
                            String rentId = scanner.nextLine();

                            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(rentId);
                            if (vehicleOpt.isEmpty()) {
                                System.err.println("Nie znaleziono pojazdu o podanym ID.");
                            } else if (vehicleOpt.get().isRented()) {
                                System.err.println("Pojazd jest już wypożyczony.");
                            } else {
                                boolean success = rentalService.rentVehicle(currentUser, vehicleOpt.get());
                                System.out.println(success ? "Pojazd wypożyczony." : "Nie udało się wypożyczyć pojazdu.");
                            }
                        } else {
                            System.err.println("Masz już wypożyczony pojazd: " + rentalRepository.findByUserId(currentUser.getId()).get().getVehicleId());
                        }
                        break;
                    case 4:
                        Optional<Rental> rentalOpt = rentalRepository.findByUserId(currentUser.getId());
                        if (rentalOpt.isEmpty()) {
                            System.out.println("Nie masz wypożyczonego pojazdu.");
                        } else {
                            boolean returned = rentalService.returnVehicle(currentUser);
                            System.out.println(returned ? "Pojazd zwrócony." : "Nie udało się zwrócić pojazdu.");
                        }
                        break;
                    case 5:
                        System.out.println("Dane użytkownika:");
                        System.out.println("Login: " + currentUser.getLogin());
                        System.out.println("Rola: " + currentUser.getRole());
                        rentalService.getUserRental(currentUser).ifPresentOrElse(
                                r -> System.out.println("Wypożyczony pojazd: " + r.getVehicleId()),
                                () -> System.out.println("Brak wypożyczonego pojazdu.")
                        );
                        break;
                    case 6:
                        if (!isAdmin) {
                            System.err.println("Brak uprawnień");
                            break;
                        }
                        System.out.println("Podaj typ pojazdu (Car/Motorcycle/Bus): ");
                        String category = scanner.nextLine();

                        System.out.print("Numer rejestracyjny: ");
                        String plate = scanner.nextLine();

                        System.out.print("Marka: ");
                        String brand = scanner.nextLine();

                        System.out.print("Model: ");
                        String model = scanner.nextLine();

                        System.out.print("Rok produkcji: ");
                        int year = Integer.parseInt(scanner.nextLine());

                        Map<String, Object> attributes = new HashMap<>();
                        if (category.equalsIgnoreCase("Bus")) {
                            System.out.print("Liczba miejsc: ");
                            attributes.put("seats", Integer.parseInt(scanner.nextLine()));
                        } else if (category.equalsIgnoreCase("Motorcycle")) {
                            System.out.print("Kategoria prawa jazdy: ");
                            attributes.put("licence_category", scanner.nextLine());
                            System.out.print("Rodzaj napędu: ");
                            attributes.put("drive", scanner.nextLine());
                        }

                        String id = vehicleRepository.generateNextAvailableId();
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
                        break;
                    case 7:
                        if (!isAdmin) {
                            System.err.println("Brak uprawnień");
                            break;
                        }
                        System.out.print("Podaj ID pojazdu do usunięcia: ");
                        String deleteId = scanner.nextLine();
                        vehicleRepository.deleteById(deleteId);
                        System.out.println("Pojazd usunięty.");
                        break;
                    case 8:
                        if (!isAdmin) {
                            System.err.println("Brak uprawnień");
                            break;
                        }
                        List<User> users = userRepository.findAll();
                        for (User u : users) {
                            System.out.println(u);
                            rentalService.getUserRental(u).ifPresentOrElse(
                                    r -> System.out.println("  Wypożyczony pojazd: " + r.getVehicleId()),
                                    () -> System.out.println("  Brak wypożyczonego pojazdu.")
                            );
                        }
                        break;
                    default:
                        System.err.println("Nieznana opcja.");
                        break;
                }
            }
        }
    }
}