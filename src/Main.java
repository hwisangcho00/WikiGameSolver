import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for the start and end pages.
        System.out.print("Enter the start page: ");
        String startTitle = scanner.nextLine();
        System.out.print("Enter the end page: ");
        String endTitle = scanner.nextLine();

        // Create the start and end page objects.
        WikipediaPage start = new WikipediaPage(startTitle);
        WikipediaPage end = new WikipediaPage(endTitle);

        // Create the game board object.
        WikipediaGameBoard gameBoard = new WikipediaGameBoard(start, end);

        // Keep playing until the start and end pages are related.
        while (!gameBoard.isRelated()) {

        }

        // Print the path.

        scanner.close();
    }

}
