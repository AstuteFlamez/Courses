import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backend for the campus path finder app
 */
public class Backend implements BackendInterface {

  // matches lines like: "Union South" -> "Atmospheric Sciences" [seconds=127.2];
  private static final Pattern EDGE_LINE =
      Pattern.compile("\"([^\"]+)\"\\s*->\\s*\"([^\"]+)\"\\s*\\[[^=\\]]*=\\s*([0-9]*\\.?[0-9]+)\\s*\\]");

  // matches a standalone node declaration: "Memorial Union";
  private static final Pattern NODE_LINE = Pattern.compile("^\\s*\"([^\"]+)\"\\s*;?\\s*$");

  private final GraphADT<String, Double> graph;

  // the graph interface has no way to list its nodes so we track them here
  private final List<String> locations = new ArrayList<>();

  /**
   * Creates a backend that stores its data in the provided graph
   *
   * @param graph the graph object this backend reads from and writes to
   */
  public Backend(GraphADT<String, Double> graph) {
    this.graph = graph;
  }

  /**
   * Clears any previously loaded graph and loads the locations and travel
   * times found in a dot file
   *
   * @param filename path to the dot file to read from
   * @throws IOException if the file cannot be found or read
   */
  @Override
  public void loadGraphData(String filename) throws IOException {
    for (String location : new ArrayList<>(locations)) {
      graph.removeNode(location);
    }
    locations.clear();

    try (Scanner in = new Scanner(new File(filename), StandardCharsets.UTF_8)) {
      while (in.hasNextLine()) {
        String line = in.nextLine();

        Matcher edge = EDGE_LINE.matcher(line);
        if (edge.find()) {
          String pred = edge.group(1);
          String succ = edge.group(2);
          double weight = Double.parseDouble(edge.group(3));
          addNode(pred);
          addNode(succ);
          graph.insertEdge(pred, succ, weight);
          continue;
        }

        Matcher node = NODE_LINE.matcher(line);
        if (node.matches()) {
          addNode(node.group(1));
        }
      }
    }
  }

  /**
   * Inserts a location into the graph and records it, unless it is already
   * being tracked
   *
   * @param location the name of the location to add
   */
  private void addNode(String location) {
    if (locations.contains(location)) {
      return;
    }
    graph.insertNode(location);
    if (graph.containsNode(location)) {
      locations.add(location);
    }
  }

  /**
   * Returns every location currently loaded
   *
   * @return a list of all location names in the graph
   */
  @Override
  public List<String> getListOfAll() {
    return new ArrayList<>(locations);
  }

  /**
   * Finds the locations along the shortest path between two locations
   *
   * @param start the location the path begins at
   * @param end the location the path ends at
   * @return the locations along the shortest path in order, or an empty list
   *         if no such path exists
   */
  @Override
  public List<String> findLocationsOnShortestPath(String start, String end) {
    try {
      return graph.shortestPathData(start, end);
    } catch (NoSuchElementException e) {
      return new ArrayList<>();
    }
  }

  /**
   * Finds the travel time of each individual step along the shortest path
   * between two locations
   *
   * @param start the location the path begins at
   * @param end the location the path ends at
   * @return the time between each pair of neighboring locations on the
   *         shortest path, or an empty list if no such path exists
   */
  @Override
  public List<Double> findTimesOnShortestPath(String start, String end) {
    List<Double> times = new ArrayList<>();
    List<String> path = findLocationsOnShortestPath(start, end);
    for (int i = 1; i < path.size(); i++) {
      try {
        times.add(graph.getEdge(path.get(i - 1), path.get(i)));
      } catch (NoSuchElementException e) {
        return new ArrayList<>();
      }
    }
    return times;
  }

  /**
   * Finds the locations that can be reached from a start location without
   * exceeding a travel time
   *
   * @param start the location to travel from
   * @param maxTime the largest travel time a location may be away from start
   * @return the locations reachable from start within maxTime, including
   *         start itself
   * @throws NoSuchElementException if start is not a location in the graph
   */
  @Override
  public List<String> getReachableFromWithin(String start, double maxTime)
      throws NoSuchElementException {
    if (!graph.containsNode(start)) {
      throw new NoSuchElementException("No location named " + start);
    }

    List<String> reachable = new ArrayList<>();
    for (String location : locations) {
      if (location.equals(start)) {
        reachable.add(location);
        continue;
      }
      try {
        if (graph.shortestPathCost(start, location) <= maxTime) {
          reachable.add(location);
        }
      } catch (NoSuchElementException e) {
        // unreachable from start -> skip it
      }
    }
    return reachable;
  }
}
