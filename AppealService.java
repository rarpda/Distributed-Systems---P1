import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for appeal service.
 **/
public interface AppealService extends Remote {

  String requestAppeal(int caseID, int prisonerID) throws RemoteException;
}
