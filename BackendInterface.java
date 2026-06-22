import java.util.List;
import java.io.IOException;

/**
 * BackendInterface - CS400 Project 1: Game Records Leaderboard
 */
public interface BackendInterface {

    //public Backend(IterableSortedCollection<GameRecord> tree)
    // Your constructor must have the signature above. All methods below must
    // use the provided tree to store, sort, and iterate through records. This
    // will enable you to create some tests that use the placeholder tree, and
    // others that make use of a working tree, depending on what is passed
    // into this constructor.

    /** Add and stores the specified record to the tree. Don't forget that the GameRecord
     *  must have the Comparator set. This will be used to store these records in order within your
     *  tree, and to retrieve them by collectables range in the getRange method.
     * @param record the game record to add
     */
    public void addRecord(GameRecord record);
  
    /**
     * Loads data from the .csv file referenced by filename.  You can rely
     * on the exact headers found in the provided records.csv, but you should
     * not rely on them always being presented in this order or on there
     * not being additional columns describing other record qualities.
     * After reading records from the file, the records are inserted into
     * the tree passed to this backend's constructor. This will be used to store these records in order within your
     * tree, and to retrieve them by score range in the getRange method.
     * @param filename is the name of the csv file to load data from
     * @throws IOException when there is trouble finding/reading file
     */
    public void readData(String filename) throws IOException;

    /**
     * Retrieves a list of names from the tree passed to the constructor.
     * The records should be ordered by the record's collectables, and fall within
     * the specified range of collectable values.  This collectables range will
     * also be used by future calls to filterRecords and getTopTen.
     *
     * If a continent filter has been set using the filterRecords method
     * below, then only records that pass that filter should be included in the
     * list of names returned by this method.
     *
     * When null is passed as either the low or high argument to this method,
     * that end of the range is understood to be unbounded.  For example, a 
     * null argument for the high parameter means that there is no maximum 
     * collectables to include in the returned list.
     *
     * @param low is the minimum collectables of records in the returned list
     * @param high is the maximum collectables of records in the returned list
     * @return List of names for all records from low to high that pass any
     *     set filter, or an empty list when no such records can be found
     */
    public List<String> getAndSetRange(Integer low, Integer high);

    /**
     * Retrieves a list of record names that have a continent that match the specified
     * continent.  
     * Similar to the getRange method: this list of record names should be ordered by the records'
     * collectables, and should only include records that fall within the specified
     * range of collectable values that was established by the most recent call
     * to getRange.  If getRange has not previously been called, then no low
     * or high collectable bound should be used.  The filter set by this method
     * will be used by future calls to the getRange and getTopTen methods.
     *
     * When null is passed as the continent to this method, then no 
     * continent filter should be used.  This clears the filter.
     *
     * @param continent filters returned record names to only include records that
     *     have a continent that match the specified value.
     * @return List of names for records that meet this filter requirement and
     *     are within any previously set collectables range, or an empty list
     *     when no such records can be found
     */
    public List<String> applyAndSetFilter(GameRecord.Continent continent);

    /**
     * This method returns a list of record names representing the top
     * ten fastest (lowest completion time) records that both fall within any attribute range specified
     * by the most recent call to getRange, and conform to any filter set by
     * the most recent call to filteredRecords.  The order of the record names 
     * in this returned list is up to you.
     *
     * If fewer than ten such records exist, return all of them.  And return an
     * empty list when there are no such records.
     *
     * @return List of ten fastest record names
     */
    public List<String> getTopTen();
}

