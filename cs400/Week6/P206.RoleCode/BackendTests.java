import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Backend class
 */
public class BackendTests {

  /**
   * Writes a small dot file using the three locations that Graph_Placeholder is
   * hard-coded with, so that loading it does not depend on the placeholder
   * being able to insert new nodes
   *
   * @return the path of the file that was written
   * @throws IOException if the file cannot be written
   */
  private String writeTestFile() throws IOException {
    File file = File.createTempFile("backendtests", ".dot");
    file.deleteOnExit();
    try (PrintWriter out = new PrintWriter(file)) {
      out.println("digraph campus {");
      out.println("\t\"Union South\" -> \"Computer Sciences and Statistics\" [seconds=1.0];");
      out.println("\t\"Computer Sciences and Statistics\" -> "
          + "\"Weeks Hall for Geological Sciences\" [seconds=2.0];");
      out.println("}");
    }
    return file.getAbsolutePath();
  }

  /**
   * Checks that loadGraphData reads locations out of a dot file and that
   * getListOfAll then reports them, and that asking for a file that is not
   * there reports the problem instead of failing quietly
   */
  @Test
  public void roleTest1() throws IOException {
    Backend backend = new Backend(new Graph_Placeholder());

    assertTrue(backend.getListOfAll().isEmpty(), "list should be empty before loading");

    backend.loadGraphData(writeTestFile());
    List<String> all = backend.getListOfAll();

    assertEquals(3, all.size(), "three distinct locations appear in the file");
    assertTrue(all.contains("Union South"));
    assertTrue(all.contains("Computer Sciences and Statistics"));
    assertTrue(all.contains("Weeks Hall for Geological Sciences"));

    // a location that is named twice in the file should only be listed once
    assertEquals(1, all.stream().filter(s -> s.equals("Computer Sciences and Statistics")).count());

    assertThrows(IOException.class, () -> backend.loadGraphData("no_such_file.dot"),
        "a missing file should throw IOException");
  }

  /**
   * Checks that findLocationsOnShortestPath returns the locations in order and
   * that findTimesOnShortestPath returns one time for each step between them
   */
  @Test
  public void roleTest2() {
    Backend backend = new Backend(new Graph_Placeholder());

    List<String> path = backend.findLocationsOnShortestPath("Union South",
        "Weeks Hall for Geological Sciences");
    assertEquals(List.of("Union South", "Computer Sciences and Statistics",
        "Weeks Hall for Geological Sciences"), path);

    // the placeholder charges 1.0 for the first step and 2.0 for the second
    List<Double> times = backend.findTimesOnShortestPath("Union South",
        "Weeks Hall for Geological Sciences");
    assertEquals(List.of(1.0, 2.0), times);
    assertEquals(path.size() - 1, times.size(), "one time per step along the path");

    // a path that never leaves its starting location has no steps to time
    List<String> samePath = backend.findLocationsOnShortestPath("Union South", "Union South");
    assertEquals(1, samePath.size());
    assertTrue(backend.findTimesOnShortestPath("Union South", "Union South").isEmpty());
  }

  /**
   * Checks that getReachableFromWithin keeps the locations within the time
   * limit, drops the ones past it, and rejects a start that is not a location
   */
  @Test
  public void roleTest3() throws IOException {
    Backend backend = new Backend(new Graph_Placeholder());
    backend.loadGraphData(writeTestFile());

    // the placeholder costs 1.0 to reach the second location and 3.0 the third
    List<String> near = backend.getReachableFromWithin("Union South", 1.0);
    assertEquals(2, near.size());
    assertTrue(near.contains("Union South"), "the start is reachable from itself");
    assertTrue(near.contains("Computer Sciences and Statistics"));
    assertFalse(near.contains("Weeks Hall for Geological Sciences"), "3.0 is over the limit");

    // raising the limit should pull in the far location
    assertEquals(3, backend.getReachableFromWithin("Union South", 3.0).size());

    assertThrows(NoSuchElementException.class,
        () -> backend.getReachableFromWithin("Camp Randall", 10.0),
        "a start that is not in the graph should throw");
  }
}
