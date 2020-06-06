import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Main class for running the client(s).
 **/
public class MainClient {
    /*Port to connect to. */
    public static final int PORT_NUMBER = 8080;

    public static void main(String[] args) {
        try {
            /* Initialise connection to server */
            Registry registry = LocateRegistry.getRegistry(PORT_NUMBER);
            ProsecutorService prosecutorStub = (ProsecutorService) registry.lookup(ProsecutorService.class.getName());
            AppealService appealStub = (AppealService) registry.lookup(AppealService.class.getName());
            System.out.println("Checking service connection: " + prosecutorStub.testConnection());

            /* Login to system. */
            PrisonerClient client = PrisonerClient.userAuthentication(prosecutorStub);

            /* Load handle decision*/
            client.prisonerDecisionHandler();

            /* Appeal to courts*/
            client.appealHandler(appealStub);

        } catch (Exception e) {
            System.err.println("An error has occurred!!\r\n" + e.toString());
        }
    }

}
