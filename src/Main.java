import java.io.IOException;
import java.util.*;

/**
 * main function of the game
 * Loops until user wants to finish playing
 * */
public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the WikiGame Solver! I'll find you link-connected path between two wikipedia pages!");

        boolean isContinue;
        do {

            String startTitle = null;
            String endTitle = null;
            WikipediaPage start = null;
            WikipediaPage end = null;
            int input = 0;
            boolean isBFS = true;
            isContinue = true;

            System.out.println("Which version do you want to use? 1) Breadth First Search 2) tf-idf");
            while (true) {
                try {
                    input = scanner.nextInt();
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    scanner.next();
                    System.out.println("Please answer in an integer value either 1 or 2");
                    continue;
                }

                if (input != 1 && input != 2) {
                    System.out.println("Please answer in an integer value either 1 or 2");
                    continue;
                }

                if (input == 2) {
                    isBFS =false;
                }

                break;
            }

            // Prompt the user for the start and end pages. If there is no page, it asks again for a valid input.
            while (true) {
                try {
                    System.out.println("Remember! You should copy and paste the wikipedia page title for the most accurate result!");
                    System.out.print("Enter the start page: ");
                    startTitle = scanner.nextLine();
                    System.out.print("Enter the end page: ");
                    endTitle = scanner.nextLine();

                    start = new WikipediaPage(startTitle, startTitle);
                    end = new WikipediaPage(endTitle, endTitle);

                } catch (IOException e) {
                    System.out.println("There is no wikipedia page with the input title. Please Input again");
                    continue;
                }
                break;
            }

            // Create the game board object.
            WikipediaGameBoard gameBoard = new WikipediaGameBoard(start, end);


            Set<String> visited = new HashSet<>();
            Map<WikipediaPage, WikipediaPage> parentMap = new HashMap<>();

            // Finds and prints the path based on the user input
            if (isBFS) {
                gameBoard.solveBFS();
            } else {
                if (!gameBoard.solveTFIDF(start, visited, parentMap)) {
                    System.out.println("Failed to find a path");
                }
            }

            System.out.println("Play Again? 1) Yes 2) No");
            while (true) {
                try {
                    input = scanner.nextInt();
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    scanner.next();
                    System.out.println("Please answer in an integer value either 1 or 2");
                    continue;
                }

                if (input != 1 && input != 2) {
                    System.out.println("Please answer in an integer value either 1 or 2");
                    continue;
                }

                if (input == 2) {
                    System.out.println("Thank you for using!");
                    isContinue = false;
                    scanner.close();
                }

                break;
            }

        } while (isContinue);


    }

}
