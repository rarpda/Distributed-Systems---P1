import java.util.HashMap;


/**
 * Class responsible for storing case information.
 **/
public class PrisonerDatabaseHandler {
  private HashMap<Integer, Case> mapOfCases = new HashMap<>(); /*Holds all information regarding cases. */

  public PrisonerDatabaseHandler() {
    /* Create 10 cases at startup and randomise P2 decision*/
    for (int caseID = 0; caseID < 10; caseID++) {
      Case newCase = new Case(caseID);
      /* Use case ID as the key*/
      mapOfCases.put(newCase.getCaseID(), newCase);
    }
  }

  /**
   * Method to get a case. Returns null if it does not exist.
   **/
  public Case getCase(int caseNumber) {
    return mapOfCases.get(caseNumber);
  }


  /**
   * Checks if the case number exists in the map.
   **/
  public boolean isCaseValid(int caseNumber) {
    return mapOfCases.containsKey(caseNumber);
  }

}

