import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.Scanner;

/**
 * This method is responsible for handling the client's interactions with the server.
 */
public class PrisonerClient {

    /*Internal variables*/
    private ProsecutorService.LoginDetails userDetails;
    private ProsecutorService prosecutorStub;
    private Scanner scannerInput = new Scanner(System.in);
    private final String YES_OPTION = "YES";
    private final String NO_OPTION = "NO";

    /**
     * Internal steps required for authentication.
     **/
    private enum authenticationSteps {
        CASE_ID,
        USER_ID,
        VERIFICATION,
        FINISHED
    }


    /**
     * Constructor for the prisoner class. Ensures the prosecutor stub is not null.
     */
    public PrisonerClient(ProsecutorService.LoginDetails userDetails, ProsecutorService prosecutorStub) {
        this.userDetails = userDetails;
        this.prosecutorStub = prosecutorStub;
        Objects.requireNonNull(prosecutorStub);
    }

    /**
     * This method displays instructions to the user.
     * This loads all the information required to make a decision from a text file.
     * It is then printed onto the system console.
     **/
    private boolean displayInstructions() {
        boolean instructionsLoaded = true;
        /*Get instruction resource. */
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream("Instructions.txt");
        if (fileStream != null) {
            /*Self closing reader. */
            try (BufferedReader instructionReader = new BufferedReader(new InputStreamReader(fileStream))) {
                String line;
                /*Read the file and print all the instructions.*/
                while ((line = instructionReader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                /*Error occurred loading the file. */
                instructionsLoaded = false;
                System.out.println("Error loading instructions: " + e.getMessage());
            }
        } else {
            instructionsLoaded = false;
        }

        return instructionsLoaded;
    }

    /**
     * This method handles the decision of the prisoner regarding the prisoner's dilemma.
     * They can choose to cooperate or betray and will get a varying reduction to their sentence.
     **/
    public void prisonerDecisionHandler() throws InternalError, RemoteException {
        boolean instructionsLoaded = displayInstructions(); /*Load instructions */
        if (instructionsLoaded) {
            System.out.println("Below are your options:");
            System.out.print("Cooperate\r\nBetray\r\nPick an option: ");
            boolean validResponseEntered;
            do {
                /*Wait for valid response. */
                validResponseEntered = processDecision(scannerInput.nextLine());
            }
            while (!validResponseEntered);
            checkSentenceStatus(); /*Check status of the sentence.*/
        } else {
            throw new InternalError("Instructions were not loaded");
        }
    }

    /**
     * This method processes the user input and sends it to the server.
     */
    private boolean processDecision(String userResponse) {
        boolean responseEntered;
        try {
            String standardisedInput = userResponse.toUpperCase(); /*Standardise input*/
            /*Check if the user input is a valid command. */
            ProsecutorService.Command commandReceived = ProsecutorService.Command.valueOf(standardisedInput);
            /* Send decision and check that the decision is valid. */
            if (prosecutorStub.logDecision(userDetails, commandReceived)) {
                responseEntered = true;
                System.out.println("Your sentence is being processed.");
            } else {
                System.out.println("Your sentence was not processed! Try again!");
                responseEntered = false;
            }
        } catch (Exception e) {
            System.err.println("Error found:" + e.getMessage());
            System.out.println(userResponse + " is not a valid option!\r\nTry again!");
            responseEntered = false;
        }
        return responseEntered;
    }

    /**
     * Method used to check status of the sentence.
     * Polls the server every 10 seconds until the other prisoner's decision becomes available.
     */
    private void checkSentenceStatus() throws RemoteException {
        /*Check every 1 second*/
        boolean sentenceAvailable = false;
        do {
            System.out.println("Checking status of sentence...");
            try {
                sentenceAvailable = prosecutorStub.hasDecisionBeenMade(userDetails);
                if (!sentenceAvailable) {
                    Thread.sleep(10000); /*Wait 10 seconds before trying again*/
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
        while (!sentenceAvailable);
        displaySentence(); /*Display the new sentence. */
    }

    /**
     * Method used to get the sentence  from the server and display it.
     */
    private void displaySentence() throws RemoteException {
        int sentenceYears = prosecutorStub.getSentence(userDetails);
        if (sentenceYears == 0) {
            /*Sentence is finished. */
            System.out.println("You are free to go!");
        } else {
            System.out.println("Your sentence is " + sentenceYears + " years.");

        }
    }

    /**
     * Method used to appeal sentence reduction.
     */
    public void appealHandler(AppealService appealStub) throws RemoteException {
        System.out.println("Do you wish to appeal your sentence?\r\n" + YES_OPTION + "\r\n" + NO_OPTION);
        System.out.print("Pick an option:");
        boolean responseEntered;
        do {
            String userInput = "";
            try {
                /*Standardise input. */
                userInput = scannerInput.nextLine().toUpperCase();
                /*Check reply is valid.*/
                if (userInput.contentEquals(YES_OPTION)) {
                    responseEntered = true;
                    /*Request appeal to server. */
                    String outcome = appealStub.requestAppeal(userDetails.getCaseID(), userDetails.getPrisonerID());
                    /*Print outcome. */
                    System.out.println(outcome);
                } else if (userInput.contentEquals(NO_OPTION)) {
                    responseEntered = true;
                } else {
                    responseEntered = false;
                }
            } catch (IllegalFormatException e) {
                responseEntered = false;
                System.out.println(userInput + " is not a valid input! Try again: ");
            }
        }
        while (!responseEntered);
    }

    /**
     * Method to handle user authentication. Returns a new instance of the client class if it passes the authentication.
     * Has 3 steps as controlled by the current state.
     **/
    public static PrisonerClient userAuthentication(ProsecutorService stub) throws RemoteException, IllegalStateException {
        PrisonerClient prisonerLoggedIn = null;
        authenticationSteps currentState = authenticationSteps.CASE_ID;
        ProsecutorService.LoginDetails loginDetails = new ProsecutorService.LoginDetails();
        Scanner scannerInput = new Scanner(System.in);
        do {
            try {
                int userInput;
                switch (currentState) {
                    case CASE_ID:
                        System.out.print("Enter the case number:");
                        userInput = Integer.parseInt(scannerInput.nextLine());
                        loginDetails.setCaseID(userInput);
                        currentState = authenticationSteps.USER_ID;
                        break;
                    case USER_ID:
                        System.out.print("Enter your ID:");
                        userInput = Integer.parseInt(scannerInput.nextLine());
                        loginDetails.setPrisonerID(userInput);
                        currentState = authenticationSteps.VERIFICATION;
                        break;
                    case VERIFICATION:
                        boolean authenticationPassed = stub.userAuthentication(loginDetails);
                        if (authenticationPassed) {
                            currentState = authenticationSteps.FINISHED;
                            prisonerLoggedIn = new PrisonerClient(loginDetails, stub);
                        } else {
                            prisonerLoggedIn = null;
                            currentState = authenticationSteps.CASE_ID;
                            System.out.println("Authentication Failed! Enter your details again!");
                        }
                        break;
                    default:
                        /*Unknown state.*/
                        throw new IllegalStateException("Current operation is undefined!!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Parameter entered is not valid! Try again: ");
            }
        }
        while (prisonerLoggedIn == null);
        return prisonerLoggedIn;
    }


}