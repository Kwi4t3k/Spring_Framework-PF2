import org.example.Car;
import org.example.Motorcycle;
import org.example.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VehicleRepositoryTest {
    private VehicleRepository vehicleRepository;

    @BeforeEach
    public void setup() {
        vehicleRepository = new VehicleRepository();
    }

    @Test
    void testAddVehiclesToFile() { // jak nie ma pliku to tworzy nowy
        Car car = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_001");
        Car car1 = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_001");
        Motorcycle motoA1 = new Motorcycle("KTM", "Duke 125", 2022, 95.0f, "MOTO_001", "A1");
        Motorcycle motoA2 = new Motorcycle("Kawasaki", "Z650", 2023, 160.0f, "MOTO_002", "A2");
        Motorcycle motoAM = new Motorcycle("Harley-Davidson", "Street Glide", 2023, 300.0f, "MOTO_003", "AM");

        vehicleRepository.addVehicle(car);
        vehicleRepository.addVehicle(car1);
        vehicleRepository.addVehicle(motoA1);
        vehicleRepository.addVehicle(motoA2);
        vehicleRepository.addVehicle(motoAM);

        vehicleRepository.save();
    }

    @Test
    void testDeleteFile() {
        File file = new File("src/main/resources/vehicles.txt");
        if(file.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }
    }

    @Test
    void testSaveAndLoad() { // test na startowo pustym pliku
        Car car = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_001");
        Motorcycle motoA1 = new Motorcycle("KTM", "Duke 125", 2022, 95.0f, "MOTO_001", "A1");

        vehicleRepository.addVehicle(car);
        vehicleRepository.addVehicle(motoA1);
        vehicleRepository.save();

        VehicleRepository newRepo = new VehicleRepository();
        assertEquals(2, newRepo.getVehicles().size());
        assertEquals("CAR_001", newRepo.getVehicles().get(0).getId());
        assertEquals("MOTO_001", newRepo.getVehicles().get(1).getId());
        assertEquals("A1", ((Motorcycle) newRepo.getVehicles().get(1)).getCategory());

        File file = new File("src/main/resources/vehicles.txt");
        if(file.delete())
        {
            System.out.println("File deleted successfully");
        }
        else
        {
            System.out.println("Failed to delete the file");
        }
    }
}