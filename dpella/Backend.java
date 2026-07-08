import com.sun.source.tree.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Backend implements BackendInterface {

    private IterableSortedCollection<GameRecord> tree = new Tree_Placeholder();

    private Integer[] collectiblesFilter = new Integer[2];

    private GameRecord.Continent continentFilter = null;

    public Backend(IterableSortedCollection<GameRecord> tree) {
        for (GameRecord record : tree)
            this.tree.insert(record);
    }

    /**
     * Add and stores the specified record to the tree. Don't forget that the GameRecord
     * must have the Comparator set. This will be used to store these records in order within your
     * tree, and to retrieve them by collectables range in the getRange method.
     * @param record the game record to add
     */
    public void addRecord(GameRecord record) {
        tree.insert(record);
    }

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
    public void readData(String filename) throws IOException {
        ArrayList<String> records = (ArrayList<String>) Files.readAllLines(Path.of(filename));
        ArrayList<String[]> recordsParsed = new ArrayList<>();

        for (String record : records)
            recordsParsed.add(parse(record));

        for (String[] fields : recordsParsed)
            this.tree.insert(new GameRecord(fields[0], GameRecord.Continent.valueOf(fields[1]), Integer.parseInt(fields[2]),
                    Integer.parseInt(fields[3]), Integer.parseInt(fields[6]), fields[8]));
    }

    private static String[] parse(String record) {
        String[] fields = new String[6];
        int start = 0;
        int end = 0;

        for (int i = 0; i < 9; i++) {
            if (record.startsWith("\"\"", start)) {
                start += 2;
                end += 2;
                fields[i--] += "\"";
            } else if (record.startsWith("\"", start)) {
                start++;
                while (true) {
                    end = record.indexOf("\"");
                    if (record.charAt(end + 1) == '\"') {
                        fields[i] += record.substring(start, ++end);
                        start = ++end;
                    } else {
                        fields[i] += record.substring(start, end++);
                        break;
                    }
                }
                start = ++end;
            } else {
                while (true) {
                    if (record.indexOf(',', start) < record.indexOf('\"', start)) {
                        end = record.indexOf(',', start);
                        fields[i] += record.substring(start, end++);
                        start = end;
                        break;
                    } else {
                        end = record.indexOf('\"', start);
                        fields[i] += record.substring(start, ++end);
                        start = ++end;
                    }
                }
            }
        }

        return fields;
    }

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
    public List<String> getAndSetRange(Integer low, Integer high) {
        collectiblesFilter = new Integer[]{low, high};
        ArrayList<GameRecord> recordList = filteredList();
        ArrayList<GameRecord> recordListFiltered = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();

        // filter by collectibles range
        for (GameRecord record : recordList) {
            for (int i = 0; i < recordListFiltered.size(); i++)
                if (record.compareTo(recordListFiltered.get(i)) < 0)
                    recordListFiltered.add(i, record);
            if (recordListFiltered.isEmpty())
                recordListFiltered.add(record);
        }

        // take name list
        for (GameRecord record : recordListFiltered)
            list.add(record.getName());

        return list;
    }

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
    public List<String> applyAndSetFilter(GameRecord.Continent continent) {
        continentFilter = continent;
        ArrayList<GameRecord> recordList = filteredList();
        ArrayList<GameRecord> recordListFiltered = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();

        // order by collectibles
        for (GameRecord record : recordList) {
            for (int i = 0; i < recordListFiltered.size(); i++)
                if (record.compareTo(recordListFiltered.get(i)) < 0)
                    recordListFiltered.add(i, record);
            if (recordListFiltered.isEmpty())
                recordListFiltered.add(record);
        }

        // take name list
        for (GameRecord record : recordListFiltered)
            list.add(record.getName());

        return list;
    }

    private ArrayList<GameRecord> filteredList() {
        // get list
        ArrayList<GameRecord> list = new ArrayList<>();
        for (GameRecord record : this.tree)
            list.add(record);

        // filter by collectibles
        list.removeIf(record -> !((collectiblesFilter[0] == null || collectiblesFilter[0] <= record.getCollectables())
                                           && (collectiblesFilter[1] == null || collectiblesFilter[1] >= record.getCollectables())));

        // filter by continent
        list.removeIf(record -> continentFilter == null || record.getContinent().equals(continentFilter));

        return list;
    }

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
    public List<String> getTopTen() {
        ArrayList<GameRecord> recordList = filteredList();
        LinkedList<GameRecord> recordListFiltered = new LinkedList<>();
        ArrayList<String> list = new ArrayList<>();

        // order by time and limit size
        for (GameRecord record : recordList) {
            for (int i = 0; i < recordListFiltered.size(); i++)
                if (compareTime(record, recordList.get(i)) < 0)
                    recordListFiltered.add(i, record);
            if (recordListFiltered.isEmpty())
                recordListFiltered.add(record);
            if (recordListFiltered.size() > 10)
                recordListFiltered.removeFirst();
        }

        // take name list
        for (GameRecord record : recordListFiltered)
            list.add(record.getName());

        return list;
    }

    private int compareTime(GameRecord record1, GameRecord record2) {
        String t1 = record1.getCompletionTime();
        String t2 = record2.getCompletionTime();

        int h1 = Integer.parseInt(t1.substring(0, 3));
        int h2 = Integer.parseInt(t2.substring(0, 3));
        if (h1 != h2) return h1 - h2;

        int m1 = Integer.parseInt(t1.substring(4, 6));
        int m2 = Integer.parseInt(t2.substring(4, 6));
        if (m1 != m2) return m1 - m2;

        int s1 = Integer.parseInt(t1.substring(7));
        int s2 = Integer.parseInt(t2.substring(7));
        return s1 - s2;
    }
}
