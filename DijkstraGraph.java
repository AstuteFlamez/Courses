import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.NoSuchElementException;

/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referenced by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode pred;

        public SearchNode(Node startNode) {
            this.node = startNode;
            this.cost = 0;
            this.pred = null;
        }

        public SearchNode(SearchNode pred, Edge newEdge) {
            this.node = newEdge.succ;
            this.cost = pred.cost + newEdge.data.doubleValue();
            this.pred = pred;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new PlaceholderMap<>());
    }

    /**
     * Insert a new directed edge with a non-negative weight into the graph. If 
     * an edge between pred and succ already exists, update the data stored in 
     * that edge to the new weight.
     * 
     * @param pred is the data contained in the new edge's predecesor node
     * @param succ is the data contained in the new edge's succ node
     * @param weight is the non-negative data to be stored in the new edge
     * @return true if the edge could be inserted or updated, or false if the 
     * pred or succ data are not found in any graph nodes or the weight 
     * specified is negative.
     */
    @Override
    public boolean insertEdge(NodeType pred, NodeType succ, EdgeType weight) {
        if (weight.doubleValue() < 0)
            return false;
        return super.insertEdge(pred, succ, weight);
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     * 
     * log(E) ≈ log(V) for a connected graph, so the overall time complexity of this
     * Complexity: E x Log(E) = O(E log V)
     * Placeholder map uses HashMap -> lookups are only O(1)
     * 
     * (number of operations) × (cost each)
     *         2E             ×    log E     =  O(E log E)
     * frontier.add() and frontier.poll()
     * 
     * BST turns graph into increasing order which is basically a LinkedList which has O(n)
     * A self balancing tree like AVL or Red-Black Tree fixes this
     *
     * @param start the starting node for the path
     * @param end   the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    protected SearchNode computeShortestPath(Node start, Node end) {
        if (start == null || end == null)
            throw new NullPointerException("start and end nodes must not be null");

        PriorityQueue<SearchNode> frontier = new PriorityQueue<>();
        PlaceholderMap<Node, Node> settled = new PlaceholderMap<>();
        frontier.add(new SearchNode(start));

        while (!frontier.isEmpty()) {
            SearchNode current = frontier.poll();
            if (settled.containsKey(current.node)) // placeholder map already has current node
                continue; 
            settled.put(current.node, current.node); // add current node to the settled set

            if (current.node == end)
                return current; // escape method

            for (Edge edge : current.node.edgesLeaving) // add all new edges of a node to priority queue
                if (!settled.containsKey(edge.succ))
                    frontier.add(new SearchNode(current, edge));
        }

        throw new NoSuchElementException(
                "No path exists from " + start.data + " to " + end.data);
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shortest path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from nodes along this shortest path
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);
        SearchNode endSearch = computeShortestPath(startNode, endNode);

        LinkedList<NodeType> path = new LinkedList<>();
        for (SearchNode current = endSearch; current != null; current = current.pred)
            path.addFirst(current.node.data); // reverse the end->start chain
        return path;
    }

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path from the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     * @throws NoSuchElementException if either the start or the end node
     * cannot be found, or there is no path from start node to end node
     * @throws NullPointerException if the start or end node are null
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        Node startNode = nodes.get(start);
        Node endNode = nodes.get(end);
        return computeShortestPath(startNode, endNode).cost;
    }

    // Part 1 (Deadline Thu Jul 16)

    /**
     * Builds the directed graph used by the tests below. 
     *
     * @return a DijkstraGraph populated with the lecture's nodes and edges
     */
    private DijkstraGraph<String, Integer> makeLectureGraph() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        
        graph.insertEdge("A", "C", 1);
        graph.insertEdge("C", "A", 1);
        graph.insertEdge("A", "B", 15);
        graph.insertEdge("B", "A", 15);
        graph.insertEdge("A", "D", 4);
        graph.insertEdge("D", "A", 4);
        graph.insertEdge("D", "B", 2);
        graph.insertEdge("B", "D", 2);
        graph.insertEdge("B", "E", 1);
        graph.insertEdge("E", "B", 1);
        graph.insertEdge("C", "E", 10);
        graph.insertEdge("E", "C", 10);
        graph.insertEdge("D", "E", 10);
        graph.insertEdge("E", "D", 10);
        return graph;
    }

    /**
     * Test 1: reproduces the exact example traced through in lecture
     */
    @Test
    public void testLecturePathAtoE() {
        DijkstraGraph<String, Integer> graph = makeLectureGraph();

        // cost of the shortest path A -> E should be 4 + 2 + 1 = 7
        assertEquals(7.0, graph.shortestPathCost("A", "E"));

        // the shortest path should visit A, D, B, E
        List<String> path = graph.shortestPathData("A", "E");
        assertEquals(List.of("A", "D", "B", "E"), path);
    }

    /**
     * Test 2: checks the cost and node sequence of the 
     * shortest path between a different start and end node
     */
    @Test
    public void testLecturePathDtoC() {
        DijkstraGraph<String, Integer> graph = makeLectureGraph();

        // cost of the shortest path D -> C should be 4 + 1 = 5
        assertEquals(5.0, graph.shortestPathCost("D", "C"));

        // the shortest path should visit D, A, C
        List<String> path = graph.shortestPathData("D", "C");
        assertEquals(List.of("D", "A", "C"), path);
    }

    /**
     * Test 3: checks the behavior when both nodes 
     * exist in the graph but there is no sequence 
     * of directed edges connecting the start to the end
     */
    @Test
    public void testNoPathBetweenExistingNodes() {
        DijkstraGraph<String, Integer> graph = makeLectureGraph();

        // add a node F connected TO the graph (F -> A) but not reachable FROM it
        graph.insertNode("F");
        graph.insertEdge("F", "A", 1);

        // both A and F exist, but no directed path leads from A to F
        assertThrows(NoSuchElementException.class,
                () -> graph.shortestPathCost("A", "F"));
        // lambda expression delays call so that the exception 
        // can be caught and tested with a try / catch
        assertThrows(NoSuchElementException.class,
                () -> graph.shortestPathData("A", "F"));


        // assertThrows(NoSuchElementException.class, graph.shortestPathCost("A", "F"));
        // without () -> , java runs shortestPathCost() first and crashes
    }

}
