import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class HashTableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

    /**
     * Inner class used to store a key, value pair together inside 
     * a LinkedList chain within the hashtable array.
     */
    protected class Pair {

        public KeyType key;
        public ValueType value;

        public Pair(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }

    }

    // The single array field used to store our key, value pairs.
    // Each index of the array holds a LinkedList (chain) of Pair objects
    // that all hashed to that index.
    protected LinkedList<Pair>[] table = null;

    // Keeps track of the number of key, value pairs currently stored.
    private int size = 0;

    /**
     * Constructs a new HashTableMap with the specified initial capacity.
     * @param capacity the initial size of the underlying array
     */
    @SuppressWarnings("unchecked")
    public HashTableMap(int capacity) {
        this.table = (LinkedList<Pair>[]) new LinkedList[capacity];
        this.size = 0;
    }

    /**
     * Constructs a new HashTableMap with a default capacity of 64.
     */
    public HashTableMap() {
        this(64);
    }

    /**
     * Adds a new key,value pair/mapping to this collection.
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        // TODO
    }

    /**
     * Checks whether a key maps to a value in this collection.
     */
    @Override
    public boolean containsKey(KeyType key) {
        // TODO
        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        // TODO
        return null;
    }

    /**
     * Remove the mapping for a key from this collection.
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        // TODO
        return null;
    }

    /**
     * Removes all key, value pairs from this collection without changing the
     * capacity of the underlying array.
     */
    @Override
    public void clear() {
        // TODO
    }

    /**
     * Retrieves the number of keys stored in this collection.
     */
    @Override
    public int getSize() {
        // TODO
        return 0;
    }

    /**
     * Retrieves this collection's capacity.
     */
    @Override
    public int getCapacity() {
        // TODO
        return 0;
    }

    /**
     * Retrieves this collection's keys.
     */
    @Override
    public List<KeyType> getKeys() {
        // TODO
        return null;
    }

    // JUnit 5 test method

    /**
     * Tests that put() correctly inserts a new key, value pair, and that
     * containsKey() and get() reflect that the pair was added.
     */
    @Test
    public void testPutAndGet() {
        HashTableMap<String, Integer> map = new HashTableMap<>();
        map.put("apple", 1);
        assertTrue(map.containsKey("apple"));
        assertEquals(1, map.get("apple"));
        assertEquals(1, map.getSize());
    }

    /**
     * Tests that put() throws an IllegalArgumentException when attempting
     * to insert a key that has already been mapped to a value, and that
     * the original mapping is left unchanged after the failed attempt.
     */
    @Test
    public void testPutDuplicateKeyThrowsException() {
        HashTableMap<String, Integer> map = new HashTableMap<>();
        map.put("apple", 1);
        assertThrows(IllegalArgumentException.class, () -> map.put("apple", 2));
        assertEquals(1, map.get("apple"));
    }

    /**
     * Tests that remove() correctly deletes a key, value pair from the collection.
     */
    @Test
    public void testRemove() {
        HashTableMap<String, Integer> map = new HashTableMap<>();
        map.put("banana", 2);
        Integer removedValue = map.remove("banana");
        assertEquals(2, removedValue);
        assertFalse(map.containsKey("banana"));
    }

    /**
     * Tests that get() throws a NoSuchElementException when called with
     * a key that has not been added to the collection.
     */
    @Test
    public void testGetNonExistentKeyThrowsException() {
        HashTableMap<String, Integer> map = new HashTableMap<>();
        assertThrows(NoSuchElementException.class, () -> map.get("missing"));
    }

    /**
     * Tests that clear() removes all key, value pairs from the collection
     */
    @Test
    public void testClear() {
        HashTableMap<String, Integer> map = new HashTableMap<>(16);
        map.put("apple", 1);
        map.put("banana", 2);
        int capacityBeforeClear = map.getCapacity();
        map.clear();
        assertEquals(0, map.getSize());
        assertFalse(map.containsKey("apple"));
        assertFalse(map.containsKey("banana"));
        assertEquals(capacityBeforeClear, map.getCapacity());
    }

}