import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;



/**
 * This class is responsible for implementing the additional functionality of the court appeal service.
 * Prisoners can appeal for a sentence reduction and this is randomly granted to them.
 * They can then receive a reduction in their sentence if successful.
 *
 **/
public class AppealServiceImpl extends UnicastRemoteObject implements AppealService {

  /*Private variables*/
  private PrisonerDatabaseHandler dbHandler;

  /**
   * This is a constructor this implementation class.
   *
   **/
  public AppealServiceImpl(PrisonerDatabaseHandler dbHandler) throws RemoteException {
    super();
    this.dbHandler = dbHandler;
    Objects.requireNonNull(this.dbHandler); /*Ensure the object is never null*/
  }

  /**
   * This method randomly decides on the outcome of the appeal.
   **/
  private boolean considerAppeal() {
    boolean appealAccepted;
    if (Math.random() < 0.5) {
      appealAccepted = false;
    } else {
      appealAccepted = true;
    }
    return appealAccepted;
  }

  /**
   * This method is reponsible for handling the prisoner's appeal request.
   * It requires all of the users' details and once the decision has been,
   * it reduces the sentence if necessary and outputs the outcome to the user.
   **/
  @Override
  public String requestAppeal(int caseID, int prisonerID) throws RemoteException, IllegalArgumentException {
    String appealOutcome;
    /* Check if ID is valid. */
    if (dbHandler.isCaseValid(caseID)) {
      /* Randomly decide if appeal has passed */
      if (considerAppeal()) {
        appealOutcome = "Appeal passed! ";
        /* Discount sentence by a year if appeal passes.*/
        Case caseFound = dbHandler.getCase(caseID);
        caseFound.reducePrisonSentence(prisonerID, 1);
        int newSentence = caseFound.getPrisonerSentence(prisonerID);
        if (newSentence == 0) {
          /*He is free to go.*/
          appealOutcome += "You are a free to go!";
        } else {
          appealOutcome += "You have to serve " + newSentence + " years.";
        }
      } else {
        appealOutcome = "Appeal request failed.";
      }
    } else {
      throw new IllegalArgumentException("Case ID is not valid");
    }
    return appealOutcome;
  }
}
