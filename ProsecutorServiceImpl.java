import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.Objects;

/**
 * This class implements the prosecutor service interface developed
 * for communication between the prisoner and the server.
 **/
public class ProsecutorServiceImpl extends UnicastRemoteObject implements ProsecutorService {

    private PrisonerDatabaseHandler dbHandler; /*Database Handler*/

    /**
     * Method to construct object.
     *
     * @param dbHandler
     **/
    public ProsecutorServiceImpl(PrisonerDatabaseHandler dbHandler) throws RemoteException {
        super();
        this.dbHandler = dbHandler;
        Objects.requireNonNull(this.dbHandler);
    }

    /**
     * Method to check if the connection is available.
     **/
    @Override
    public String testConnection() throws RemoteException {
        return "Hello from server!";
    }


    /**
     * Method for logging decision of the prisoner.
     * Requires login details the actual decision made.
     **/
    @Override
    public synchronized boolean logDecision(LoginDetails details, Command decision) throws RemoteException {
        boolean decisionLogged = true;
        /*Input verification. */
        if (userAuthentication(details) && (decision != null)) {
            Case caseFound = dbHandler.getCase(details.getCaseID()); /*Get case*/
            switch (decision) {
                case BETRAY:
                    System.out.println("Case ID: " + caseFound.getCaseID() + ". Prisoner " + details.getPrisonerID() + " decided to betray");
                    caseFound.logPrisonerDecision(details.getPrisonerID(), true);
                    break;
                case COOPERATE:
                    caseFound.logPrisonerDecision(details.getPrisonerID(), false);
                    System.out.println("Case ID: " + caseFound.getCaseID() + ". Prisoner " + details.getPrisonerID() + " decided to cooperate");
                    break;
                default:
                    throw new RemoteException("Command not supported!");
            }
        } else {
            decisionLogged = false;
        }
        return decisionLogged;
    }


    /**
     * Method to check if both the prisoners have replied.
     **/
    @Override
    public boolean hasDecisionBeenMade(LoginDetails details) throws RemoteException {
        /* Input verification*/
        if (userAuthentication(details)) {
            return dbHandler.getCase(details.getCaseID()).isSentencingAvailable();
        } else {
            return false;
        }
    }


    /**
     * Method used to get the prisoner's sentence.
     **/
    @Override
    public int getSentence(LoginDetails details) throws RemoteException, IllegalArgumentException {
        /* Input verification*/
        if (userAuthentication(details)) {
            Case caseFound = dbHandler.getCase(details.getCaseID());
            return caseFound.getPrisonerSentence(details.getPrisonerID());
        } else {
            throw new IllegalArgumentException("The ID and the case are not valid!");
        }
    }


    /**
     * Method to
     */
    @Override
    public boolean userAuthentication(LoginDetails details) throws RemoteException {
        /*Input verification. */
        if (details != null) {
            boolean caseValid = dbHandler.isCaseValid(details.getCaseID()); /*Case ID check */
            int id = details.getPrisonerID(); /*prisoner ID*/
            boolean prisonerIDValid = (id == 0 || id == 1); /*Validity check*/
            return caseValid && prisonerIDValid;
        } else {
            return false;
        }
    }
}
