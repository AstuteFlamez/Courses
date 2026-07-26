import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

// Tests for the Frontend class. Each test builds a Frontend on top of the
// placeholder backend and graph, then checks the HTML that comes back.
public class FrontendTests {

    // Checks that the shortest path prompt has both text boxes with the
    // required ids and a button with the right label on it.
    @Test
    public void roleTest1() {
        Frontend frontend = new Frontend(
            new Backend_Placeholder(new Graph_Placeholder()));

        String html = frontend.generateShortestPathPromptHTML();

        // both text boxes need these exact ids
        assertTrue(html.contains("id=\"start\""),
            "prompt is missing a text field with id=start");
        assertTrue(html.contains("id=\"end\""),
            "prompt is missing a text field with id=end");

        // there should be two text inputs and the button
        assertTrue(html.contains("type=\"text\""),
            "prompt is missing a text input");
        assertTrue(html.contains("Find Shortest Path"),
            "prompt is missing the Find Shortest Path button");
    }

    // Checks the shortest path results. The path should show up as a numbered
    // list with the total time, and a bad start should give an error message
    // instead of a list.
    @Test
    public void roleTest2() {
        Frontend frontend = new Frontend(
            new Backend_Placeholder(new Graph_Placeholder()));

        // the placeholder graph knows this path, so we should get 3 stops
        String html = frontend.generateShortestPathResponseHTML(
            "Union South", "Weeks Hall for Geological Sciences");

        // the stops belong in an ordered list
        assertTrue(html.contains("<ol>") && html.contains("</ol>"),
            "response is missing an ordered list");
        assertTrue(html.contains("<li>Union South</li>"),
            "response is missing the start location");
        assertTrue(html.contains("<li>Computer Sciences and Statistics</li>"),
            "response is missing the middle location");
        assertTrue(html.contains("<li>Weeks Hall for Geological Sciences</li>"),
            "response is missing the end location");

        // the placeholder returns times of 1.0, 2.0, and 3.0, so 6.0 total
        assertTrue(html.contains("6.0"),
            "response is missing the correct total travel time");

        // this location is not in the placeholder graph, so there is no path
        String noPath = frontend.generateShortestPathResponseHTML(
            "Bascom Hall", "Union South");

        assertFalse(noPath.contains("<ol>"),
            "a missing path should not print a list of stops");
        assertTrue(noPath.contains("<p>"),
            "a missing path should still explain the problem in a paragraph");
    }

    // Checks the reachable from within prompt and its results. The prompt
    // needs both ids and the button, and the results should be a bulleted
    // list of locations.
    @Test
    public void roleTest3() {
        Frontend frontend = new Frontend(
            new Backend_Placeholder(new Graph_Placeholder()));

        String prompt = frontend.generateReachableFromWithinPromptHTML();

        assertTrue(prompt.contains("id=\"from\""),
            "prompt is missing a text field with id=from");
        assertTrue(prompt.contains("id=\"time\""),
            "prompt is missing a text field with id=time");
        assertTrue(prompt.contains("Reachable From Within"),
            "prompt is missing the Reachable From Within button");

        // the placeholder always hands back all three of its locations
        String html = frontend.generateReachableFromWithinResponseHTML(
            "Union South", 30.0);

        // these locations are not in any order, so they go in a bulleted list
        assertTrue(html.contains("<ul>") && html.contains("</ul>"),
            "response is missing an unordered list");
        assertTrue(html.contains("<li>Union South</li>"),
            "response is missing a reachable location");
        assertTrue(html.contains("<li>Weeks Hall for Geological Sciences</li>"),
            "response is missing a reachable location");

        // the paragraph should say where we started and how long we can travel
        assertTrue(html.contains("Union South") && html.contains("30.0"),
            "response is missing the start location or the max time");
    }
}
    
