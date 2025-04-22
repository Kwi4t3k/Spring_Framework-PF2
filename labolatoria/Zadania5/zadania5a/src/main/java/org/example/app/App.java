package org.example.app;

import org.example.models.User;
import org.example.models.Rental;
import org.example.models.Vehicle;
import org.example.services.impl.AuthHibernateService;
import org.example.services.impl.RentalHibernateService;
import org.example.services.impl.VehicleHibernateService;

import java.util.*;

public class App {
    private final AuthHibernateService authService;
    private final VehicleHibernateService vehicleService;
    private final RentalHibernateService rentalService;
    private final Scanner scanner = new Scanner(System.in);

    public App(AuthHibernateService authHibernateService, VehicleHibernateService vehicleHibernateService, RentalHibernateService rentalHibernateService) {
        this.authService = authHibernateService;
        this.vehicleService = vehicleHibernateService;
        this.rentalService = rentalHibernateService;
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

        String password;

        while (true) {
            System.out.print("Wprowadź hasło: ");
            password = scanner.nextLine();

            System.out.print("Wprowadź ponownie hasło: ");
            String passwordConfirm = scanner.nextLine();

            if (password.equals(passwordConfirm)) {
                break;
            }
            System.err.println("Hasła nie są zgodne. Spróbuj ponownie.");
        }

        boolean success = authService.register(newLogin, password, "USER");
        if (success) {
            System.out.println("Zarejestrowano pomyślnie. Zaloguj się teraz.");
        }
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
        List<Vehicle> vehicles = rented ? vehicleService.findAll() : vehicleService.findAvailableVehicles();

        if (rented) {
            vehicles = vehicles.stream()
                    .filter(v -> !vehicleService.isAvailable(v.getId()))
                    .toList();
        }

        if (vehicles.isEmpty()) {
            System.out.println("Brak pojazdów");
        } else {
            vehicles.forEach(System.out::println);
        }
    }

    private void rentVehicle(User user) {
        boolean hasActiveRental = rentalService.findAll().stream()
                .anyMatch(r -> r.getUser().getId().equals(user.getId()) && r.getReturnDate() == null);

        if (hasActiveRental) {
            System.out.println("Masz już wypożyczony pojazd. Zwróć go zanim wypożyczysz kolejny.");
            return;
        }

        System.out.print("ID pojazdu do wypożyczenia: ");
        String id = scanner.nextLine();

        if (!vehicleService.isAvailable(id)) {
            System.out.println("Pojazd jest już wypożyczony.");
            return;
        }

        try {
            rentalService.rent(id, user.getId());
            System.out.println("Pojazd wypożyczony!");
        } catch (Exception e) {
            System.out.println("Błąd: " + e.getMessage());
        }
    }

    private void returnVehicle(User user) {
        List<Rental> rentals = rentalService.findAll();

        rentals.stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .filter(r -> r.getReturnDate() == null)
                .findFirst()
                .ifPresentOrElse(rental -> {
                    boolean success = rentalService.returnRental(rental.getVehicle().getId(), user.getId());
                    if (success) {
                        System.out.println("Pojazd został zwrócony.");
                    } else {
                        System.err.println("Nie udało się zwrócić pojazdu.");
                    }
                }, () -> System.out.println("Nie masz wypożyczonego pojazdu."));
    }

    private void showUserInfo(User user) {
        System.out.println("Dane użytkownika:");
        System.out.println("Login: " + user.getLogin());
        System.out.println("Rola: " + user.getRole());

        Optional<Rental> rentalOptional = rentalService.findAll().stream()
                .filter(r -> r.getUser().getId().equals(user.getId()))
                .filter(r -> r.getReturnDate() == null)
                .findFirst();

        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();
            System.out.println("Wypożyczony pojazd: " + rental.getVehicle().getId());
        } else {
            System.out.println("Nie masz wypożyczonego pojazdu");
        }
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
            attributes.put("seats", Integer.parseInt(scanner.nextLine()));
        } else if (category.equalsIgnoreCase("Motorcycle")) {
            System.out.print("Kategoria prawa jazdy: ");
            attributes.put("licence_category", scanner.nextLine());
            System.out.print("Rodzaj napędu: ");
            attributes.put("drive", scanner.nextLine());
        }

        Vehicle newVehicle = Vehicle.builder()
                        .id(UUID.randomUUID().toString())
                        .category(category)
                        .plate(plate)
                        .brand(brand)
                        .model(model)
                        .year(year)
                        .price(price)
                        .attributes(attributes)
                        .build();

        vehicleService.save(newVehicle);
        System.out.println("Dodano pojazd: " + newVehicle);
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();

        vehicleService.deleteById(id);
        System.out.println("Usunięto pojazd o id: " + id);
    }

    private void listUsers() {
        List<User> users = authService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników.");
        } else {
            users.forEach(System.out::println);
        }
    }
}