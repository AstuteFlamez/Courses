import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * This class extends the HashTableMap class to run submission checks on it.
 */
public class P207SubmissionChecker extends HashTableMap<Integer, Integer> {

  /**
   * Checks if hashtable resizes as expected.
   */
  @Test
  public void testTableResize() {
    HashTableMap<String, String> map = new HashTableMap<>(8);
    map.put("a", "1");
    map.put("b", "2");
    map.put("c", "3");
    map.put("d", "4");
    map.put("e", "5");

    // check capacity before resizing
    Assertions.assertEquals(8, map.getCapacity());   
 
    map.put("f", "6");

    // check capacity after resizing
    Assertions.assertEquals(16, map.getCapacity());
  }

  /**
   * Tests putting duplicate "a" keys into the HashTableMap and checks for IllegalArgumentExceptions
   * after the first insertion. Then checkes if calling get("a") returns the first value inserted.
   */
  @Test
  public void testDuplicateInsertions() {
    HashTableMap<String, Integer> map = new HashTableMap<>();
    map.put("a", 6);
    Assertions.assertThrows(
           IllegalArgumentException.class,
           () -> map.put("a", 7)
    );
    Assertions.assertThrows(
           IllegalArgumentException.class,
           () -> map.put("a", 8)
    );
    Assertions.assertEquals(6, map.get("a"));
  }
 
  /**
   * Tests the get method on a small HashTableMap with keys 1, 2, and 3.
   */
  @Test
  public void testGetOnSmallMap123() {
    HashTableMap<Integer, String> map = new HashTableMap<>();
    map.put(1, "one");
    map.put(2, "two");
    map.put(3, "three");
    Assertions.assertEquals("two", map.get(2));
  }
  
}
