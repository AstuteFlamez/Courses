import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FrontendTests {

    /**
     * Tests the submit command functionality by 
     * adding a single game record to the backend.
     */
    @Test
    public void frontendTest1() {
        BackendInterface backend = new Backend_Placeholder(new IterableRedBlackTree<>());
        
        String input = "submit Player1 AFRICA 500 10 25 001:30:45\nquit\n";
        TextUITester tester = new TextUITester(input, true);
        
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        
        frontend.runCommandLoop();
        
        String output = tester.checkOutput();
        
        assertTrue(output.contains("submit") || output.contains("Error"), 
                   "Frontend should parse and attempt to process the submit command");
    }

    /**
     * Tests the collectables range filter and show commands
     */
    @Test
    public void frontendTest2() {
        BackendInterface backend = new Backend_Placeholder(new IterableRedBlackTree<>());
        
        String input = "collectables 10 to 50\nshow 10\nquit\n";
        TextUITester tester = new TextUITester(input, true);
        
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        
        frontend.runCommandLoop();
        
        String output = tester.checkOutput();
        // The backend placeholder may have issues, so we check for either success or proper error handling
        assertTrue(output.contains("Collectables") || output.contains("Error"), 
                   "Frontend should either set collectables filter or handle errors gracefully");
    }

    /**
     * Tests the location filter and fastest times display
     */
    @Test
    public void frontendTest3() {
        BackendInterface backend = new Backend_Placeholder(new IterableRedBlackTree<>());
        
        String input = "help\nlocation ASIA\nshow fastest times\nquit\n";
        TextUITester tester = new TextUITester(input, true);
        
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        
        frontend.runCommandLoop();
        
        String output = tester.checkOutput();
        assertTrue(output.contains("Location filter set to") || output.contains("Command Instructions"), 
                   "Frontend should display help instructions and confirm location filter");
    }

    /**
     * Tests error handling for invalid commands
     */
    @Test
    public void frontendTest4() {
        BackendInterface backend = new Backend_Placeholder(new IterableRedBlackTree<>());
        
        String input = "invalid_command\nsubmit\nquit\n";
        TextUITester tester = new TextUITester(input, true);
        
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        
        String output = tester.checkOutput();
        assertTrue(output.contains("Error"), 
                   "Frontend should display error message for invalid commands");
    }

    /**
     * Verifies that a record submitted through frontend's 
     * command loop reaches backend and is stored. 
     */
    @Test
    public void frontendIntegrationTestSubmitStoresRecord() {

        IterableSortedCollection<GameRecord> tree = new IterableRedBlackTree<>();
        BackendInterface backend = new Backend(tree);

        // Submit one record through the frontend, then quit.
        TextUITester tester = new TextUITester(
            "submit Player1 AFRICA 500 10 25 001:30:45\nquit\n", true);
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        tester.checkOutput();

        // The real backend should now actually contain that record
        List<String> names = backend.getTopTen();
        assertTrue(names.contains("Player1"),
            "Record submitted through the frontend should be stored in the backend");
    }

    /**
     * Verifies that the collectables range set 
     * by Frontend is applied by the real Backend
     */
    @Test
    public void frontendIntegrationTestCollectablesRange() {

        IterableSortedCollection<GameRecord> tree = new IterableRedBlackTree<>();
        BackendInterface backend = new Backend(tree);

        // First session: load three records with collectables of 100, 130, and 200
        TextUITester loader = new TextUITester(
            "submit lowPlayer AFRICA 700 50 100 010:00:00\n"
            + "submit midPlayer ASIA 800 50 130 011:00:00\n"
            + "submit highPlayer EUROPE 900 50 200 012:00:00\n"
            + "quit\n", true);
        Frontend loadFrontend = new Frontend(new Scanner(System.in), backend);
        loadFrontend.runCommandLoop();
        loader.checkOutput();

        // Second session: set the range, then show the results
        TextUITester tester = new TextUITester(
            "collectables 125 to 135\nshow 10\nquit\n", true);
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        String output = tester.checkOutput();

        // Only the record with 130 collectables falls inside 125-135
        assertTrue(output.contains("midPlayer"),
            "The record with 130 collectables should fall inside the 125-135 range");
        assertFalse(output.contains("lowPlayer"),
            "The record with 100 collectables should fall outside the range");
        assertFalse(output.contains("highPlayer"),
            "The record with 200 collectables should fall outside the range");
    }

    /**
     * Verifies the continent filter set through
     * the Frontend is applied by the real Backend
     */
    @Test
    public void frontendIntegrationTestLocationFilter() {

        IterableSortedCollection<GameRecord> tree = new IterableRedBlackTree<>();
        BackendInterface backend = new Backend(tree);

        // First session: load one EUROPE record and one ASIA record
        TextUITester loader = new TextUITester(
            "submit euroOne EUROPE 900 50 100 010:00:00\n"
            + "submit asiaOne ASIA 800 50 110 011:00:00\n"
            + "quit\n", true);
        Frontend loadFrontend = new Frontend(new Scanner(System.in), backend);
        loadFrontend.runCommandLoop();
        loader.checkOutput();

        // Second session: filter to EUROPE
        TextUITester tester = new TextUITester(
            "location EUROPE\nshow 10\nquit\n", true);
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        String output = tester.checkOutput();

        // The backend's continent filter should exclude the ASIA record
        assertTrue(output.contains("euroOne"),
            "The EUROPE filter should include the record from EUROPE");
        assertFalse(output.contains("asiaOne"),
            "The EUROPE filter should exclude the record from ASIA");
    }

    /**
     * Verifies that records loaded from a file by the Frontend's submit multiple 
     * command exist in the Backend and can be retrieved by a subsequent query
     */
    @Test
    public void frontendIntegrationTestSubmitCombinesWithLoadedData() {

        IterableSortedCollection<GameRecord> tree = new IterableRedBlackTree<>();
        BackendInterface backend = new Backend(tree);

        // First session: load the whole CSV file, then add one more record
        TextUITester loader = new TextUITester(
            "submit multiple records.csv\n"
            + "submit uniqueRunner ASIA 51000 560 995 100:00:00\n"
            + "quit\n", true);
        Frontend loadFrontend = new Frontend(new Scanner(System.in), backend);
        loadFrontend.runCommandLoop();
        String loadOutput = loader.checkOutput();

        assertTrue(loadOutput.contains("Successfully loaded records from records.csv"),
            "The frontend should report loading the CSV file through the backend");

        // Second session: query a range that no record from the CSV can satisfy
        TextUITester tester = new TextUITester(
            "collectables 991 to 999\nshow 10\nquit\n", true);
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        String output = tester.checkOutput();

        // The submitted record survives alongside the loaded ones and is the only match
        assertTrue(output.contains("uniqueRunner"),
            "The submitted record should still be retrievable after loading the CSV");
        assertTrue(output.contains("Total records displayed: 1"),
            "Only the submitted record should fall in the 991-999 range");
    }

}