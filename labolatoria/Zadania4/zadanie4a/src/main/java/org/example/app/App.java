package org.example.app;

import org.example.models.Rental;
import org.example.models.User;
import org.example.models.Vehicle;
import org.example.services.AuthService;
import org.example.services.RentalService;
import org.example.services.VehicleService;

import java.util.*;

public class App {
    private final AuthService authService;
    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public App(AuthService authService, VehicleService vehicleService, RentalService rentalService) {
        this.authService = authService;
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
    }

    public void run() {
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
                    registerUser();
                }
                continue;
            }

            User currentUser = userOpt.get();
            boolean isAdmin = currentUser.getRole().equalsIgnoreCase("ADMIN");
            boolean loggedIn = true;

            System.out.println("Witaj, " + currentUser.getLogin() + "!");

            while (loggedIn) {
                showMenu(isAdmin);

                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case -1 -> {
                        if (isAdmin) {
                            running = false;
                            loggedIn = false;
                            System.out.println("Do widzenia");
                        } else System.err.println("Brak uprawnień");
                    }
                    case 0 -> {
                        loggedIn = false;
                        System.out.println("Wylogowano: " + currentUser.getLogin());
                    }
                    case 1 -> showVehicles(false);
                    case 2 -> showVehicles(true);
                    case 3 -> rentVehicle(currentUser);
                    case 4 -> returnVehicle(currentUser);
                    case 5 -> showUserInfo(currentUser);
                    case 6 -> {
                        if (isAdmin) addVehicle();
                        else System.err.println("Brak uprawnień");
                    }
                    case 7 -> {
                        if (isAdmin) removeVehicle();
                        else System.err.println("Brak uprawnień");
                    }
                    case 8 -> {
                        if (isAdmin) listUsers();
                        else System.err.println("Brak uprawnień");
                    }
                    default -> System.err.println("Nieznana opcja.");
                }
            }
        }
    }

    private void registerUser() {
        System.out.println("** Rejestracja **");
        System.out.print("Wprowadź nowy login: ");
        String newLogin = scanner.nextLine();

        if (authService.isLoginTaken(newLogin)) {
            System.err.println("Taki login już istnieje.");
            return;
        }

        String newPassword = "";
        String newPasswordConfirm = "";

        while (true) {
            System.out.print("Wprowadź hasło: ");
            newPassword = scanner.nextLine();

            System.out.print("Wprowadź ponownie hasło: ");
            newPasswordConfirm = scanner.nextLine();

            if (newPassword.equals(newPasswordConfirm)) {
                break;
            } else {
                System.err.println("Hasła nie są zgodne. Spróbuj ponownie.");
            }
        }

        authService.register(newLogin, newPassword, "USER");
        System.out.println("Zarejestrowano pomyślnie. Zaloguj się teraz.");
    }

    private void showMenu(boolean isAdmin) {
        System.out.println("\n** Menu **");
        if (isAdmin) System.out.println("-1 - Zakończ program");
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
    }

    private void showVehicles(boolean rented) {
        List<Vehicle> list = rented ? vehicleService.getRentedVehicles() : vehicleService.getAvailableVehicles();

        if (list.isEmpty()) {
            System.out.println(rented ? "Brak wypożyczonych pojazdów" : "Brak dostępnych pojazdów");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void rentVehicle(User user) {
        if (rentalService.getUserRental(user).isPresent()) {
            System.err.println("Masz już wypożyczony pojazd.");
            return;
        }

        System.out.print("Podaj ID pojazdu do wypożyczenia: ");
        String rentId = scanner.nextLine();

        Optional<Vehicle> vehicleOpt = vehicleService.findById(rentId);

        if (vehicleOpt.isEmpty()) {
            System.err.println("Nie znaleziono pojazdu o podanym ID.");
        } else {
            Vehicle vehicle = vehicleOpt.get();

            Optional<Rental> rentalOpt = rentalService.getRentalByVehicleId(vehicle.getId());

            if (rentalOpt.isPresent()) {
                System.err.println("Pojazd o ID: " + vehicle.getId() + " jest już wypożyczony.");
            } else {
                boolean success = rentalService.rentVehicle(user, vehicle);

                if (success) {
                    System.out.println("Pojazd o ID: " + vehicle.getId() + " został wypożyczony.");
                } else {
                    System.err.println("Nie udało się wypożyczyć pojazdu.");
                }
            }
        }
    }

    private void returnVehicle(User user) {
        Optional<Rental> rentalOpt = rentalService.getUserRental(user);

        if (rentalOpt.isEmpty()) {
            System.out.println("Nie masz wypożyczonego pojazdu.");
        } else {
            boolean success = rentalService.returnVehicle(user);

            if (success) {
                System.out.println("Pojazd został zwrócony.");
            } else {
                System.err.println("Nie udało się zwrócić pojazdu.");
            }
        }
    }

    private void showUserInfo(User user) {
        System.out.println("Dane użytkownika:");
        System.out.println("Login: " + user.getLogin());
        System.out.println("Rola: " + user.getRole());
        rentalService.getUserRental(user).ifPresentOrElse(
                r -> System.out.println("Wypożyczony pojazd: " + r.getVehicleId()),
                () -> System.out.println("Brak wypożyczonego pojazdu.")
        );
    }

    private void addVehicle() {
        System.out.print("Typ pojazdu (Car/Motorcycle/Bus): ");
        String category = scanner.nextLine();
        System.out.print("Numer rejestracyjny: ");
        String plate = scanner.nextLine();
        System.out.print("Marka: ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Rok produkcji: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Cena: ");
        double price = Double.parseDouble(scanner.nextLine());

        Map<String, Object> attributes = new HashMap<>();
        if (category.equalsIgnoreCase("Bus")) {
            System.out.print("Liczba miejsc: ");
            attributes.put("seats", Optional.of(Integer.parseInt(scanner.nextLine())));
        } else if (category.equalsIgnoreCase("Motorcycle")) {
            System.out.print("Kategoria prawa jazdy: ");
            attributes.put("licence_category", scanner.nextLine());
            System.out.print("Rodzaj napędu: ");
            attributes.put("drive", scanner.nextLine());
        }

        Vehicle newVehicle = vehicleService.saveVehicle(
                Vehicle.builder()
                        .category(category)
                        .plate(plate)
                        .brand(brand)
                        .model(model)
                        .year(year)
                        .price(price)
                        .attributes(attributes)
                        .build()
        );
        System.out.println("Dodano pojazd: " + newVehicle);
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();

        Optional<Vehicle> vehicleOpt = vehicleService.findById(id);
        if (vehicleOpt.isPresent()) {
            vehicleService.deleteVehicle(id);
            System.out.println("Usunięto pojazd o ID: " + id);
        } else {
            System.err.println("Nie znaleziono pojazdu o podanym ID.");
        }
    }

    private void listUsers() {
        List<User> users = authService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
        } else {
            for (User u : users) {
                System.out.println(u);
                rentalService.getUserRental(u).ifPresentOrElse(
                        r -> System.out.println("  Wypożyczony pojazd: " + r.getVehicleId()),
                        () -> System.out.println("  Brak wypożyczonego pojazdu.")
                );
            }
        }
    }
}