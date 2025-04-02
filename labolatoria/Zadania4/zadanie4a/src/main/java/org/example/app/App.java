//import org.example.db.JdbcConnectionManager;
//
//import java.sql.Connection;
//
//public static void main(String[] args) {
//    try (Connection connection = JdbcConnectionManager.getInstance().getConnection()) {
//        System.out.println("Połączenie udane!");
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
//---------------------------------------------------------------------------------------


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
                        if (isAdmin) addVehicle(); else System.err.println("Brak uprawnień");
                    }
                    case 7 -> {
                        if (isAdmin) removeVehicle(); else System.err.println("Brak uprawnień");
                    }
                    case 8 -> {
                        if (isAdmin) listUsers(); else System.err.println("Brak uprawnień");
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

        System.out.print("Wprowadź hasło: ");
        String newPassword = scanner.nextLine();
        System.out.print("Podaj rolę (ADMIN / USER): ");
        String role = scanner.nextLine().toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("USER")) {
            System.err.println("Niepoprawna rola. Domyślnie ustawiono USER.");
            role = "USER";
        }

        authService.register(newLogin, newPassword, role);
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
        } else if (vehicleOpt.get().isRented()) {
            System.err.println("Pojazd już jest wypożyczony.");
        } else {
            boolean success = rentalService.rentVehicle(user, vehicleOpt.get());
            System.out.println(success ? "Pojazd wypożyczony." : "Nie udało się wypożyczyć pojazdu.");
        }
    }

    private void returnVehicle(User user) {
        Optional<Rental> rental = rentalService.getUserRental(user);
        if (rental.isEmpty()) {
            System.out.println("Nie masz wypożyczonego pojazdu.");
        } else {
            boolean success = rentalService.returnVehicle(user);
            System.out.println(success ? "Pojazd zwrócony." : "Nie udało się zwrócić pojazdu.");
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
            attributes.put("seats", Integer.parseInt(scanner.nextLine()));
        } else if (category.equalsIgnoreCase("Motorcycle")) {
            System.out.print("Kategoria prawa jazdy: ");
            attributes.put("licence_category", scanner.nextLine());
            System.out.print("Rodzaj napędu: ");
            attributes.put("drive", scanner.nextLine());
        }

        Vehicle newVehicle = vehicleService.createVehicle(category, brand, model, year, plate, price, attributes);
        System.out.println("Dodano pojazd: " + newVehicle);
    }

    private void removeVehicle() {
        System.out.print("Podaj ID pojazdu do usunięcia: ");
        String id = scanner.nextLine();
        vehicleService.removeById(id);
        System.out.println("Usunięto pojazd.");
    }

    private void listUsers() {
        List<User> users = authService.getAllUsers();
        for (User u : users) {
            System.out.println(u);
            rentalService.getUserRental(u).ifPresentOrElse(
                    r -> System.out.println("  Wypożyczony pojazd: " + r.getVehicleId()),
                    () -> System.out.println("  Brak wypożyczonego pojazdu.")
            );
        }
    }
}