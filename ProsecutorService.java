import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProsecutorService extends Remote {
    enum Command {
        BETRAY("BETRAY"),
        COOPERATE("COOPERATE");

        String commandName;

        Command(String commandName) {
            this.commandName = commandName;
        }
    }

    class LoginDetails implements Serializable {
        private int caseID;
        private int prisonerID;

        public int getCaseID() {
            return caseID;
        }

        public void setCaseID(int caseID) {
            this.caseID = caseID;
        }

        public int getPrisonerID() {
            return prisonerID;
        }

        public void setPrisonerID(int prisonerID) {
            this.prisonerID = prisonerID;
        }


    }

    boolean logDecision(LoginDetails details, Command decision) throws RemoteException;

    boolean hasDecisionBeenMade(LoginDetails details) throws RemoteException;

    String testConnection() throws RemoteException;

    int getSentence(LoginDetails details) throws RemoteException, IllegalArgumentException;

    boolean userAuthentication(LoginDetails details) throws RemoteException;
}