package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        IVehicleRepository vehicleRepository = new VehicleRepository();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWypożyczalnia pojazdów\n");
            System.out.println("1 - Wyświetl dostępne pojazdy");
            System.out.println("2 - Wyświetl wypożyczone pojazdy");
            System.out.println("3 - Wypożycz pojazd");
            System.out.println("4 - Zwróć pojazd");
            System.out.println("5 - Wyjście\n");
            System.out.print("Wybierz opcję:");

            int wybor = scanner.nextInt();
            scanner.nextLine();

            if (wybor == 1) {
                boolean ok = false;
                for (Vehicle vehicle : vehicleRepository.getVehicles()) {
                    if (!vehicle.rented) {
                        System.out.println(vehicle);
                        ok = true;
                    }
                }
                if (!ok) {
                    System.out.println("Brak pojazdów\n");
                }
            } else if (wybor == 2) {
                boolean ok = false;
                for (Vehicle vehicle : vehicleRepository.getVehicles()) {
                    if (vehicle.rented) {
                        System.out.println(vehicle);
                        ok = true;
                    }
                }
                if (!ok) {
                    System.out.println("Brak pojazdów\n");
                }
            } else if (wybor == 3) {
                vehicleRepository.rentVehicle(scanner.nextLine());
            } else if (wybor == 4) {
                vehicleRepository.returnVehicle(scanner.nextLine());
            } else if (wybor == 5) {
                System.out.println("Do widzenia");
                scanner.close();
                return;
            }
        }
    }
}