import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service.BankService;
import model.Customer;
/**
 *
 * @author Kwize
 */


public class TestConnection {
    public static void main(String[] args) {
        try {
            // 1. Dial the Server (Port 1099)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // 2. Ask for the "BankingService"
            BankService service = (BankService) registry.lookup("BankingService");

            // 3. Test it!
            System.out.println("✅ CONNECTION SUCCESS! Found the Server.");
            
            // Optional: Try to fetch data
            // System.out.println(service.findAllCustomers());

        } catch (Exception e) {
            System.err.println("❌ CONNECTION FAILED. Is the Server running?");
            e.printStackTrace();
        }
    }
}