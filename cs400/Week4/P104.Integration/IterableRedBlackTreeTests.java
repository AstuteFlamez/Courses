import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IterableRedBlackTreeTests {

    /**
     * Test in-order iteration over integers without iterator bounds
     * Verifies that duplicates are preserved and ordering is ascending
     */
    @Test
    @DisplayName("Integer iteration without bounds returns sorted duplicates")
    public void testIntegerIterationNoBounds() {
        IterableRedBlackTree<Integer> tree = new IterableRedBlackTree<>();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(3); // duplicate
        tree.insert(9);
        tree.insert(1);

        // iterate without setting min/max
        List<Integer> got = new ArrayList<>();
        for (Integer v : tree) {
            got.add(v);
        }

        // expected sorted order including duplicates
        List<Integer> expected = Arrays.asList(1, 3, 3, 5, 7, 9);
        assertEquals(expected, got);
    }

    /**
     * Test iteration over strings with a specified start (minimum) bound only
     * Verifies that values >= min are returned in ascending order
     */
    @Test
    @DisplayName("String iteration with minimum bound only")
    public void testStringIterationWithMinOnly() {
        IterableRedBlackTree<String> tree = new IterableRedBlackTree<>();
        tree.insert("dog");
        tree.insert("cat");
        tree.insert("apple");
        tree.insert("banana");

        tree.setIteratorMin("banana");

        List<String> got = new ArrayList<>();
        for (String s : tree) {
            got.add(s);
        }

        // sorted tree: apple, banana, cat, dog -> after min banana: banana, cat, dog
        List<String> expected = Arrays.asList("banana", "cat", "dog");
        assertEquals(expected, got);
    }

    /**
     * Test iteration with both a specified minimum and maximum bound
     * Verifies that returned values lie within [min, max] and maintain order, including duplicates
     */
    @Test
    @DisplayName("Integer iteration with min and max bounds")
    public void testIntegerIterationWithMinAndMax() {
        IterableRedBlackTree<Integer> tree = new IterableRedBlackTree<>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(5); // duplicate
        tree.insert(12);
        tree.insert(8);
        tree.insert(6);

        // set bounds to include values from 5 through 8 (inclusive)
        tree.setIteratorMin(5);
        tree.setIteratorMax(8);

        List<Integer> got = new ArrayList<>();
        for (Integer v : tree) {
            got.add(v);
        }

        // sorted full order: 3,5,5,6,7,8,10,12 -> within [5,8]: 5,5,6,7,8
        List<Integer> expected = Arrays.asList(5, 5, 6, 7, 8);
        assertEquals(expected, got);
    }

}
