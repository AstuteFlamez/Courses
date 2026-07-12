import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Paths;

public class Backend implements BackendInterface {

    public IterableSortedCollection<GameRecord> tree;
    private Integer low;
    private Integer high;
    private GameRecord.Continent continentFilter;

    public Backend(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree;
        this.low = null;
        this.high = null;
        this.continentFilter = null;
    }

    /** Add and stores the specified record to the tree. Don't forget that the GameRecord
     *  must have the Comparator set. This will be used to store these records in order within your
     *  tree, and to retrieve them by collectables range in the getRange method.
     * @param record the game record to add
     */
    @Override
    public void addRecord(GameRecord record) {
        this.tree.insert(record);
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
    @Override
    public void readData(String filename) throws IOException {
        List<String> headerCategories = new ArrayList<>();

        try (Scanner scanner = new Scanner(Paths.get(filename))) {

            //Setting header categories indices to -1
            int nameIndex = -1;
            int continentIndex = -1;
            int scoreIndex = -1;
            int max_healthIndex = -1;
            int collectablesIndex = -1;
            int completion_timeIndex = -1;

            //Adding first line - header to headerCategories
            while (scanner.hasNextLine()) {
                String row = scanner.nextLine();
                String[] headerParts = row.split(",");
                for (int i = 0; i < headerParts.length; i++) {
                    headerCategories.add(headerParts[i]);
                }
                //Sets the index according to header name in case of shuffling of headers
                //Header index remains -1 if it is missing in the provided CSV file
                for (int i = 0; i < headerCategories.size(); i++) {
                    if (headerCategories.get(i).trim().equalsIgnoreCase("name")) {
                        nameIndex = i;
                    } else if (headerCategories.get(i).trim().equalsIgnoreCase("continent")) {
                        continentIndex = i;
                    } else if (headerCategories.get(i).trim().equalsIgnoreCase("score")) {
                        scoreIndex = i;
                    } else if (headerCategories.get(i).trim().equalsIgnoreCase("max_health")) {
                        max_healthIndex = i;
                    } else if (headerCategories.get(i).trim().equalsIgnoreCase("collectables")) {
                        collectablesIndex = i;
                    } else if (headerCategories.get(i).trim().equalsIgnoreCase("completion_time")) {
                        completion_timeIndex = i;
                    } else {
                        continue;
                    }
                }
                break;
            }
            //Continuing to parse file from second line
            while (scanner.hasNextLine()) {

                String row = scanner.nextLine();
                String[] recordParts = row.split(",");

                String name;
                GameRecord.Continent location;
                int score;
                int maxHealth;
                int collectables;
                String completionTime;

                //Checking that nameIndex is within bounds (not -1 and not greater than number of columns in given row)
                if (nameIndex >= 0 && nameIndex < recordParts.length) {
                    if (!recordParts[nameIndex].trim().isEmpty()) {
                        name  = recordParts[nameIndex];
                    } else {
                        name = "";
                    }
                } else {
                    name = "";
                }

                //Checking that continentIndex is within bounds
                if (continentIndex >= 0 && continentIndex < recordParts.length) {
                    if (!recordParts[continentIndex].trim().isEmpty()) {
                        if (recordParts[continentIndex].trim().equalsIgnoreCase("AFRICA")) {
                            location = GameRecord.Continent.AFRICA;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("ASIA")) {
                            location = GameRecord.Continent.ASIA;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("ANTARCTICA")) {
                            location = GameRecord.Continent.ANTARCTICA;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("AUSTRALIA")) {
                            location = GameRecord.Continent.AUSTRALIA;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("EUROPE")) {
                            location = GameRecord.Continent.EUROPE;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("NORTH AMERICA")) {
                            location = GameRecord.Continent.NORTH_AMERICA;
                        } else if (recordParts[continentIndex].trim().equalsIgnoreCase("SOUTH AMERICA")) {
                            location = GameRecord.Continent.SOUTH_AMERICA;
                        } else {
                            location = null;
                        }
                    } else {
                        location = null;
                    }
                } else {
                    location = null;
                }

                //Checking that scoreIndex is within bounds
                if (scoreIndex >= 0 && scoreIndex < recordParts.length) {
                    if (!recordParts[scoreIndex].trim().isEmpty()) {
                        score = Integer.parseInt(recordParts[scoreIndex]);
                    } else {
                        score = 0;
                    }
                } else {
                    score = 0;
                }

                //Checking that max_healthIndex is within bounds
                if (max_healthIndex >= 0 && max_healthIndex < recordParts.length) {
                    if (!recordParts[max_healthIndex].trim().isEmpty()) {
                        maxHealth = Integer.parseInt(recordParts[max_healthIndex]);
                    } else {
                        maxHealth = 0;
                    }
                } else {
                    maxHealth = 0;
                }

                //Checking that collectablesIndex is within bounds
                if (collectablesIndex >= 0 && collectablesIndex < recordParts.length) {
                    if (!recordParts[collectablesIndex].trim().isEmpty()) {
                        collectables = Integer.parseInt(recordParts[collectablesIndex]);
                    } else {
                        collectables = 0;
                    }
                } else {
                    collectables = 0;
                }

                //Checking that completion_timeIndex is within bounds
                if (completion_timeIndex >= 0 && completion_timeIndex < recordParts.length) {
                    if (!recordParts[completion_timeIndex].trim().isEmpty()) {
                        completionTime = recordParts[completion_timeIndex].trim();
                    } else {
                        completionTime = "";
                    }
                } else {
                    completionTime = "";
                }

                //Creating a new record with the above headers as required by GameRecord
                GameRecord record = new GameRecord(name, location, score, maxHealth, collectables, completionTime);
                this.addRecord(record);
            }

        } catch (Exception e) {
            System.out.println("Unexpected exception thrown!" + e.getMessage());
        }
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
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        this.low = low;
        this.high = high;

        if (low == null) {
            this.tree.setIteratorMin(null);
        } else {
            //Since setIteratorMin takes in value as GameRecord instead of Integer
            this.tree.setIteratorMin(new GameRecord(null, null, 0, 0, this.low , null));
        }

        if (high == null) {
            this.tree.setIteratorMax(null);
        } else {
            //Since setIteratorMax takes in value as GameRecord instead of Integer
            this.tree.setIteratorMax(new GameRecord(null, null, 0, 0, this.high , null));
        }

        List<String> recordNames = new ArrayList<>();
        for (GameRecord gr : this.tree) {
            //Checking if gr's continent is same as currently set continent filter
            if (this.continentFilter == null || gr.getContinent() == this.continentFilter) {
                //recordNames will only contain records within given range and with continent specified (if any)
                recordNames.add(gr.getName());
            }
        }

        return recordNames;
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
    @Override
    public List<String> applyAndSetFilter(GameRecord.Continent continent) {
        this.continentFilter = continent;

        //Setting range filter (if any)
        List<String> recordNames = getAndSetRange(this.low, this.high);

        List<String> rangeFilteredNames = new ArrayList<>();

        for (GameRecord gr : this.tree) {
            if ((recordNames.contains(gr.getName())) &&
                    (this.continentFilter == null || gr.getContinent() == this.continentFilter)) {
                //rangeFilteredNames will only contain records within given range and with continent specified (if any)
                rangeFilteredNames.add(gr.getName());
            }
        }
        return rangeFilteredNames;
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
    @Override
    public List<String> getTopTen() {

        List<String> topTenFastest = new ArrayList<>();

        //Calling applyAndSetFilter is enough because applyAndSetFilter also calls getAndSetRange
        List<String> rangeFilteredNames = applyAndSetFilter(this.continentFilter);

        List<GameRecord> passedRangeAndContinentFilter = new ArrayList<>();

        //Finding records associated with the names  that passed range and continent filters
        for (GameRecord gr : this.tree) {
            if (rangeFilteredNames.contains(gr.getName())) {
                passedRangeAndContinentFilter.add(gr);
            }
        }

        //Sorting the list basis completion time
        for (int i = 0; i < passedRangeAndContinentFilter.size(); i++) {
            for (int j = i + 1; j < passedRangeAndContinentFilter.size(); j++) {
                long timeToSecondsA = changeTimeToSeconds(passedRangeAndContinentFilter.get(i).getCompletionTime());
                long timeToSecondsB = changeTimeToSeconds(passedRangeAndContinentFilter.get(j).getCompletionTime());
                if (timeToSecondsA > timeToSecondsB) {
                    GameRecord temp = passedRangeAndContinentFilter.get(i);
                    passedRangeAndContinentFilter.set(i, passedRangeAndContinentFilter.get(j));
                    passedRangeAndContinentFilter.set(j, temp);
                }
            }
        }

        for (int i = 0; i < passedRangeAndContinentFilter.size() && i < 10; i++) {
            topTenFastest.add(passedRangeAndContinentFilter.get(i).getName());
        }

        return topTenFastest;
    }

    /**
     * This method returns given time in HH:MM:SS format converted into seconds
     *
     * @param time in String format of hours:minutes:seconds
     * @return total time in seconds
     */
    private long changeTimeToSeconds (String time) {
        long hours = 0;
        long minutes = 0;
        long seconds = 0;

        String[] parts = time.split(":");

        hours = Integer.parseInt(parts[0]);
        minutes = Integer.parseInt(parts[1]);
        seconds = Integer.parseInt(parts[2]);

        long totalTimeInSeconds = (hours * 3600) + (minutes * 60) + seconds;

        return totalTimeInSeconds;
    }
}