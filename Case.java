/**
 * This class represents a prison case where there are two prisoners.
 * It has an ID and ideally would be stored in a database.
 */
public class Case {

    /* Static variables */
    public static final int NUMBER_PRISONERS = 2;
    public static final int PRISONER1_ID = 0;
    public static final int PRISONER2_ID = 1;

    /*Private parameters */
    private caseStatus currentStatus;
    private int caseID;
    private Prisoner[] prisoner = new Prisoner[NUMBER_PRISONERS];

    /**
     * Object constructor.
     **/
    public Case(int caseID) {
        this.caseID = caseID;
        this.currentStatus = caseStatus.OPEN;
    }

    /*
     * Enumeration for case status.
     * */
    enum caseStatus {
        OPEN,
        WAITING_REPLY,
        DECISION_MADE,
    }


    public int getCaseID() {
        return caseID;
    }

    /**
     * Method that checks if prisoner ID is valid.
     **/
    private boolean accessValidation(int prisonerID) {
        if (prisonerID >= 0 && prisonerID < prisoner.length) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This methods logs the prisoners' decision (if they betrayed their partner or not).
     **/
    public synchronized void logPrisonerDecision(int prisonerID, boolean betrayedPartner) {
        /*Check input. Only allow the prisoner to decide once. */
        if (accessValidation(prisonerID) && prisoner[prisonerID] == null) {
            /*Initialise prisoner depending on ID. */
            prisoner[prisonerID] = new Prisoner(betrayedPartner);
            currentStatus = caseStatus.WAITING_REPLY; /*Wait for other prisoner to reply. */
            calculateSentenceReduction(); /* Calculate the sentence reduction.*/
        } else {
            throw new IllegalArgumentException("Index is out of bounds!");
        }
    }

    /**
     * Method used to externally reduce the prison sentence by higher authority (Appeals Court).
     */
    public void reducePrisonSentence(int prisonerID, int sentenceReduction) {
        if (accessValidation(prisonerID)) {
            /*Reduce sentence only if access validation passes.*/
            prisoner[prisonerID].reduceSentence(sentenceReduction);
        }
    }

    /**
     * Method used to poll if decision is available.
     **/
    public synchronized boolean isSentencingAvailable() {
        if (currentStatus == caseStatus.DECISION_MADE) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method processes the decision of both users and reduces the sentence accordingly.
     **/
    private void calculateSentenceReduction() {
        /*Decision not made yet. Waiting for other prisoner. */
        if ((currentStatus != caseStatus.DECISION_MADE) && (prisoner[PRISONER1_ID] == null || prisoner[PRISONER2_ID] == null)) {
            currentStatus = caseStatus.WAITING_REPLY;
        } else {
            /*Calculate decision. Implements table discussed. */
            if (prisoner[PRISONER1_ID].hasBetrayedPartner()) {
                if (prisoner[PRISONER2_ID].hasBetrayedPartner()) {
                    prisoner[PRISONER1_ID].reduceSentence(1);
                    prisoner[PRISONER2_ID].reduceSentence(1);
                } else {
                    prisoner[PRISONER1_ID].reduceSentence(3);
                    prisoner[PRISONER2_ID].reduceSentence(2);
                }
            } else {
                if (prisoner[PRISONER2_ID].hasBetrayedPartner()) {
                    prisoner[PRISONER1_ID].reduceSentence(2);
                    prisoner[PRISONER2_ID].reduceSentence(3);
                } else {
                    prisoner[PRISONER1_ID].reduceSentence(5);
                    prisoner[PRISONER2_ID].reduceSentence(5);
                }
            }
            /*Decision has been set.*/
            currentStatus = caseStatus.DECISION_MADE;
        }
    }

    /**
     * Method used to get the prisoner's sentence.
     */
    public int getPrisonerSentence(int prisonerID) {
        if (accessValidation(prisonerID)) {
            /* Get the sentence for the prisoner. */
            return prisoner[prisonerID].getYearsReceived();
        } else {
            throw new IllegalArgumentException("ID is not valid.");
        }
    }

    /**
     * Class that represents one of the user, the prisoner.
     * It contains the year and if they betrayed their partner.
     * Initialised only if the prisoner has made a decision.
     */
    static class Prisoner {

        public int getYearsReceived() {
            return yearsReceived;
        }

        public boolean hasBetrayedPartner() {
            return betrayedPartner;
        }

        public void reduceSentence(int yearsToReduce) {
            int newSentence = yearsReceived - yearsToReduce;
            if (newSentence < 0) {
                yearsReceived = 0;
            } else {
                yearsReceived = newSentence;
            }
        }

        private int yearsReceived;
        private boolean betrayedPartner;

        public Prisoner(boolean betrayedPartner) {
            /* Randomly generate sentence.*/
            this.yearsReceived = (int) (Math.random() * 24) + 3;
            this.betrayedPartner = betrayedPartner;
        }
    }
}