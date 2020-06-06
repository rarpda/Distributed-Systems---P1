import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class PrisonServer {

    public static final int PORT_NUMBER = 8080; /* Port where services are registered to.*/

    public static void main(String args[]) {
        try {
            /* Initialise information holder. */
            PrisonerDatabaseHandler dbHandler = new PrisonerDatabaseHandler();

            /*Create implementations of all services and add them to the registry at port 8080.*/
            ProsecutorServiceImpl prosecutorStub = new ProsecutorServiceImpl(dbHandler);
            AppealServiceImpl appealStub = new AppealServiceImpl(dbHandler);
            Registry registry = LocateRegistry.getRegistry(PORT_NUMBER);
            /* Rebind to previous stub location. Allows for server to restart without having to kill RMI registered port. */
            registry.rebind(ProsecutorService.class.getName(), prosecutorStub);
            registry.rebind(AppealService.class.getName(), appealStub);
            System.out.println("Server ready for requests. ");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
        }
    }
}