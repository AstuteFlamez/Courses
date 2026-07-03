import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.time.Duration;

/**
 * Opaque-box test class used to test each teammate's Backend implementation
 * of BackendInterface. These tests only call methods declared in
 * BackendInterface, so the exact same test class can be compiled and run
 * against any teammate's Backend.java by adjusting the classpath.
 *
 * IMPORTANT: These tests are written against the documented behavior of
 * Tree_Placeholder, which (a) only retains the single most-recently-inserted
 * record, and (b) otherwise iterates over three fixed records ordered by
 * collectables: speedRoyalty (120, EUROPE), xXxgamer47xXx (130, ASIA),
 * v0idt3mp0 (140, AFRICA). Tests therefore assert on those known records
 * rather than assuming the tree stores arbitrary inserted data.
 */
public class TeamTests {

    /**
     * Tests that getAndSetRange returns record names restricted to the given
     * collectables range. Using the placeholder's three fixed records
     * (collectables 120/130/140), a range of [125, 145] should include
     * xXxgamer47xXx (130) and v0idt3mp0 (140) but exclude speedRoyalty (120).
     */
    @Test
    public void testGetAndSetRangeExcludesOutOfRange() {
        // create a fresh backend backed by the teammate's tree implementation
        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend(tree);

        // query for records with collectables between 125 and 145 inclusive
        List<String> names = backend.getAndSetRange(125, 145);

        // speedRoyalty has 120 collectables, which is below 125, so it must
        // be excluded; the other two fixed records fall inside the range
        assertFalse(names.contains("speedRoyalty"),
                "Record with 120 collectables should be excluded from [125,145]");
        assertTrue(names.contains("xXxgamer47xXx"),
                "Record with 130 collectables should be included in [125,145]");
    }

    /**
     * Tests that applyAndSetFilter restricts results to records whose continent
     * matches the given filter. Filtering the placeholder's fixed records by
     * ASIA should include xXxgamer47xXx (ASIA) and exclude speedRoyalty
     * (EUROPE) and v0idt3mp0 (AFRICA). Passing null should clear the filter.
     */
    @Test
    public void testApplyAndSetFilterByContinent() {
        // create a fresh backend backed by the teammate's tree implementation
        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend(tree);

        // filter to only ASIA records; wrapped in a timeout so an infinite
        // loop in the implementation fails cleanly instead of hanging the JVM
        List<String> asiaOnly = assertTimeoutPreemptively(Duration.ofSeconds(3), () ->
            backend.applyAndSetFilter(GameRecord.Continent.ASIA)
        );

        // only the ASIA record should remain after filtering
        assertTrue(asiaOnly.contains("xXxgamer47xXx"),
                "ASIA record should pass the ASIA filter");
        assertFalse(asiaOnly.contains("speedRoyalty"),
                "EUROPE record should not pass the ASIA filter");

        // clearing the filter (null) should let a non-ASIA record back in
        List<String> cleared = assertTimeoutPreemptively(Duration.ofSeconds(3), () ->
            backend.applyAndSetFilter(null)
        );
        assertTrue(cleared.contains("speedRoyalty"),
                "Filter cleared: EUROPE record should be included again");
    }

    /**
     * Tests that readData loads records from a CSV file without throwing, and
     * that after loading, a query returns a non-empty list of names. The
     * placeholder always iterates at least its three fixed records, so a
     * successful read followed by an unbounded query must be non-empty.
     */
    @Test
    public void testReadDataLoadsWithoutError() {
        // create a fresh backend backed by the teammate's tree implementation
        IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();
        BackendInterface backend = new Backend(tree);

        // reading a valid CSV should complete without throwing an IOException
        try {
            backend.readData("records.csv");
        } catch (IOException e) {
            fail("readData threw IOException while reading records.csv: " + e.getMessage());
        }

        // an unbounded query should return a non-empty list, since the
        // placeholder always iterates its fixed records at minimum
        List<String> allNames = backend.getAndSetRange(null, null);
        assertFalse(allNames.isEmpty(),
                "Expected a non-empty list of names after reading records.csv");
    }
}
