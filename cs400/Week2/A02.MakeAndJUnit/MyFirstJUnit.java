import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Some tests for MyList implementation of ListADT.
 */
public class MyFirstJUnit {

    /** 
     * Checks the size of a new list, the size after adding to the list,
     * and the size after clearing it's contents.
     * @returns true when this test passes, otherwise false
     */
    @Test
    public void testSizeOfList() {
        MyList<Boolean> list = new MyList<>();
        assertEquals(0, list.size(), "Newly created list is not empty.");
    
        list.add(true);
        list.add(false);
        list.add(true);
        list.add(false);
        assertEquals(4, list.size(), "List size is not 4 after calling add 4 times");

        list.clear();
        assertEquals(0, list.size(), "List size is not 0 after clearing");
    }
    
    /**
     * By inserting 100 numbers this test should help ensure that the 
     * capacity of the underlying array is able to grow multiple times.
     * We then check that every pair of sequential elements in this list
     * are still in ascending order.
     * @returns true when this test passes, otherwise false
     */
    @Test
    public void testInserting100Numbers() {
        MyList<Integer> list = new MyList<>();
        // fill list with ITERATIONS sequentail and increasing elements
        final int ITERATIONS = 100;
        for(int i=0;i<ITERATIONS;i++)
            list.add(i+1);
        // ensure that elements are still in increasing order
        for(int i=0;i<ITERATIONS-1;i++)
            assertTrue(list.get(i) < list.get(i+1), "Found non-ascending elements: "+list.get(i)+
                ", "+list.get(i+1)+" at indexes "+i+", and "+(i+1));
    }

    /**
     * Removes an element from each end of the array, and then checks what is
     * left in the position of that removed element to test correctnesss.  This
     * method is also relying on and testing size() to compute last index.
     * @returns true when this test passes, otherwise false
     */
    @Test
    public void testRemovingFromEnds() {
        MyList<String> list = new MyList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");
        list.add("durian");
        
        // test removal from the (highest index) end of the list
        String threeWas = list.remove(list.size()-1);
        String twoIs = list.get(list.size()-1);
        assertEquals("durian", threeWas, "Problem removing durian from the end of the list");
        assertEquals("cherry", twoIs, "Problem removing durian from the end of the list, so that cherry is the new final element.");

        // test removal from the (lowest index) start of the list
        String zeroWas = list.remove(0);
        String zeroIs = list.get(0);
        assertEquals("apple", zeroWas, "Problem removing apple from start of the list");
        assertEquals("banana", zeroIs, "Problem removing apple from start of the list, so that banana is the new first element.");
    }
    
    /**
     * A standalone test to verify MyList capacity expansion.
     * @returns true when this test passes, otherwise false
     */
    @Test
    public void testCapacityExpansionPrecondition() {
        MyList<Integer> list = new MyList<>();

        // Fill with initial capacity (20 elements)
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        // Precondition check: abort if list size isn't 20
        assertEquals(20, list.size(), "Expected size 20 after adding 20 elements, but got " + list.size() + " - Failed");

        // Trigger capacity doubling by adding one more element
        list.add(21);

        // Verify the new size is 21
        assertEquals(21, list.size(), "Expected size 21 after adding 21st element, but got " + list.size() + " - Failed");
    }
}
