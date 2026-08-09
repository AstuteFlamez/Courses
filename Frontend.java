import java.util.List;
import java.util.NoSuchElementException;

// This class makes the HTML that gets shown to the user.
// It asks the backend for the actual path and location data.
public class Frontend implements FrontendInterface {

    private BackendInterface backend;

    // Save the backend so the other methods can use it.
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    // Makes two text boxes and a button for the shortest path search.
    public String generateShortestPathPromptHTML() {
        String html = "";
        html += "<label for=\"start\">Start Location: </label>";
        html += "<input type=\"text\" id=\"start\" />";
        html += "<label for=\"end\">End Location: </label>";
        html += "<input type=\"text\" id=\"end\" />";
        html += "<button>Find Shortest Path</button>";
        return html;
    }

    // Shows the path from start to end as a numbered list, plus the total time.
    public String generateShortestPathResponseHTML(String start, String end) {
        List<String> locations = backend.findLocationsOnShortestPath(start, end);

        // An empty list means there is no path.
        if (locations == null || locations.size() == 0) {
            return "<p>No path could be found from " + start + " to " + end
                + ".</p>";
        }

        // Add up all the times to get the total.
        List<Double> times = backend.findTimesOnShortestPath(start, end);
        double totalTime = 0.0;
        for (int i = 0; i < times.size(); i++) {
            totalTime += times.get(i);
        }

        String html = "";
        html += "<p>Shortest path from " + start + " to " + end + ":</p>";
        html += "<ol>";
        for (int i = 0; i < locations.size(); i++) {
            html += "<li>" + locations.get(i) + "</li>";
        }
        html += "</ol>";
        html += "<p>Total travel time: " + totalTime + " minutes</p>";
        return html;
    }

    // Makes two text boxes and a button for the reachable from within search.
    public String generateReachableFromWithinPromptHTML() {
        String html = "";
        html += "<label for=\"from\">Start Location: </label>";
        html += "<input type=\"text\" id=\"from\" />";
        html += "<label for=\"time\">Maximum Travel Time (minutes): </label>";
        html += "<input type=\"text\" id=\"time\" />";
        html += "<button>Reachable From Within</button>";
        return html;
    }

    // Shows every location you can get to from start within maxTime minutes.
    public String generateReachableFromWithinResponseHTML(String start,
        double maxTime) {

        List<String> locations;

        // The backend throws an exception if the start location is not real.
        try {
            locations = backend.getReachableFromWithin(start, maxTime);
        } catch (NoSuchElementException e) {
            return "<p>The location " + start + " could not be found.</p>";
        }

        if (locations == null || locations.size() == 0) {
            return "<p>No locations could be reached from " + start
                + " within " + maxTime + " minutes.</p>";
        }

        String html = "";
        html += "<p>Locations reachable from " + start + " within " + maxTime
            + " minutes:</p>";
        html += "<ul>";
        for (int i = 0; i < locations.size(); i++) {
            html += "<li>" + locations.get(i) + "</li>";
        }
        html += "</ul>";
        return html;
    }
}
    

