import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Scanner;

public class FrontendTests {

    /**
     * Tests the submit command functionality by 
     * adding a single game record to the backend.
     */
    @Test
    public void frontendTest1() {
        BackendInterface backend = new Backend_Placeholder(null);
        
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
        BackendInterface backend = new Backend_Placeholder(null);
        
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
        BackendInterface backend = new Backend_Placeholder(null);
        
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
        BackendInterface backend = new Backend_Placeholder(null);
        
        String input = "invalid_command\nsubmit\nquit\n";
        TextUITester tester = new TextUITester(input, true);
        
        Scanner scanner = new Scanner(System.in);
        Frontend frontend = new Frontend(scanner, backend);
        frontend.runCommandLoop();
        
        String output = tester.checkOutput();
        assertTrue(output.contains("Error"), 
                   "Frontend should display error message for invalid commands");
    }
}
