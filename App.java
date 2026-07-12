import java.util.Scanner;

/**
 * Main entry point for running the CS400 Project 1: Game Records Leaderboard app.
 */
public class App {
    public static void main(String[] args) {
        IterableSortedCollection<GameRecord> tree = new IterableRedBlackTree<>(); 
        BackendInterface backend = new Backend(tree);
        Scanner in = new Scanner(System.in);
        FrontendInterface frontend = new Frontend(in,backend);

        System.out.println("Welcome to the Leaderboards");
        System.out.println("==================");

        frontend.runCommandLoop();

        System.out.println();
        System.out.println("====================");
        System.out.println("Thanks, and Goodbye.");
    }
}
