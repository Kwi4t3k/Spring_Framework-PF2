import org.example.models.Vehicle;
import org.example.repositories.VehicleRepository;
import org.example.repositories.impl.VehicleJsonRepository;

import java.util.Scanner;

public class vehiclesTest {
    public static void main(String[] args) {
        VehicleRepository vehicleRepository = new VehicleJsonRepository();

//        System.out.println("== TEST: WSZYSTKIE POJAZDY Z BAZY ==");
//        for (Vehicle v : vehicleRepository.findAll()) {
//            System.out.println("ID: " + v.getId() + ", model: " + v.getModel());
//        }

//        for (Vehicle v : vehicleRepository.findAll()) {
//            System.out.println("ID: " + v.getId() + ", Plate: " + v.getPlate() + ", Rented: " + v.isRented());
//            System.out.println("Category: " + v.getCategory() + ", Attributes: " + v.getAttributes());
//        }
        System.out.println("DEBUG: Lista dostępnych ID:");
        for (Vehicle v : vehicleRepository.findAll()) {
            System.out.println("ID: '" + v.getId() + "' | model: " + v.getModel());
        }

        System.out.print("Podaj id pojazdu do wypożyczenia: ");
        Scanner scanner = new Scanner(System.in);
        String rentId = scanner.nextLine().trim();
        System.out.println("DEBUG: Wpisane ID: '" + rentId + "'");
    }
}
