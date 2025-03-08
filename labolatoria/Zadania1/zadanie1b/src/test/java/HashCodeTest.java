import org.example.Car;
import org.example.Vehicle;

public class HashCodeTest {
    public static void main(String[] args) {
        Vehicle car1 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001");
        Vehicle car2 = new Car("BMW", "M3", 2022, 300.0f, "CAR_001"); // Ten sam ID
        Vehicle car3 = new Car("Audi", "A4", 2021, 250.0f, "CAR_002"); // Inny ID

        System.out.println("car1.hashCode() == car2.hashCode(): " + (car1.hashCode() == car2.hashCode())); // ✅ true
        System.out.println("car1.hashCode() == car3.hashCode(): " + (car1.hashCode() == car3.hashCode())); // ❌ false
    }
}
