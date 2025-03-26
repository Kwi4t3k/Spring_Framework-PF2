//import org.example.Car;
//import org.example.models.Vehicle;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//public class VehicleTest {
//    @Test
//    void testEquals() {
//        Vehicle car1 = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_001");
//        Vehicle car2 = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_001");
//        Vehicle car3 = new Car("BMW", "Series 3", 2021, 180.0f, "CAR_002");
//
//        assertEquals(car1, car2, "Pojazdy z tym samym ID powinny być równe");
//        assertNotEquals(car1, car3, "Pojazdy z różnymi ID powinny być różne");
//    }
//
//    @Test
//    void testHashCode() {
//        Vehicle car1 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001");
//        Vehicle car2 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001"); // Ten sam ID
//        Vehicle car3 = new Car("Audi", "A4", 2021, 250.0f, "CAR_002"); // Inny ID
//
//        // Sprawdzamy poprawność hashCode()
//        assertEquals(car1.hashCode(), car2.hashCode(), "Pojazdy z tym samym ID powinny mieć taki sam hashCode");
//        assertNotEquals(car1.hashCode(), car3.hashCode(), "Pojazdy z różnymi ID powinny mieć różne hashCode");
//    }
//
//    @Test
//    void testCollectionBehavior() {
//        Set<Vehicle> vehicles = new HashSet<>();
//
//        Vehicle car1 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001");
//        Vehicle car2 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001"); // Ten sam ID
//        Vehicle car3 = new Car("Audi", "A4", 2021, 250.0f, "CAR_002"); // Inny ID
//
//        vehicles.add(car1);
//        vehicles.add(car2); // Powinno zostać zignorowane
//        vehicles.add(car3); // Powinno zostać dodane
//
//        assertEquals(2, vehicles.size(), "HashSet powinien przechowywać tylko unikalne ID pojazdów");
//    }
//}