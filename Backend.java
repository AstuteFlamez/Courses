import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/**
 * Backend implementation that stores and retrieves game records.
 */
public class Backend implements BackendInterface {

    // The tree used to store all game records.
    private IterableSortedCollection<GameRecord> tree;

    // The current collectables range.
    private Integer low;
    private Integer high;

    // The current continent filter.
    private GameRecord.Continent filter;

    /**
     * Creates a backend that uses the provided tree.
     *
     * @param tree tree used to store the game records
     */
    public Backend(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree;
        this.low = null;
        this.high = null;
        this.filter = null;
    }

    /**
     * Adds one game record to the tree.
     *
     * @param record record to add
     */
    @Override
    public void addRecord(GameRecord record) {
        tree.insert(record);
    }

    /**
     * Reads game records from a CSV file and stores them in the tree.
     *
     * @param filename name or path of the CSV file
     * @throws IOException if the file cannot be read or is missing required headers
     */
    @Override
    public void readData(String filename) throws IOException {
        try (Scanner scanner = new Scanner(new File(filename))) {

            if (!scanner.hasNextLine()) {
                throw new IOException("CSV file is empty.");
            }

            // Read the first line and find the location of each required column.
            String[] headers = scanner.nextLine().split(",");

            int nameCol = -1;
            int continentCol = -1;
            int scoreCol = -1;
            int healthCol = -1;
            int collectCol = -1;
            int timeCol = -1;

            for (int i = 0; i < headers.length; i++) {
                // Lowercase allows NAME, Name, and name to all work.
                String header = headers[i].trim().toLowerCase();

                if (header.equals("name")) {
                    nameCol = i;
                } else if (header.equals("continent")) {
                    continentCol = i;
                } else if (header.equals("score")) {
                    scoreCol = i;
                } else if (header.equals("max_health")) {
                    healthCol = i;
                } else if (header.equals("collectables")) {
                    collectCol = i;
                } else if (header.equals("completion_time")) {
                    timeCol = i;
                }
            }

            // An index of -1 means that the required header was not found.
            if (nameCol == -1
                    || continentCol == -1
                    || scoreCol == -1
                    || healthCol == -1
                    || collectCol == -1
                    || timeCol == -1) {
                throw new IOException(
                        "CSV file is missing one or more required headers.");
            }

            // Find the largest required column index.
            int largestRequiredColumn = Math.max(
                    Math.max(Math.max(nameCol, continentCol),
                            Math.max(scoreCol, healthCol)),
                    Math.max(collectCol, timeCol));

            // Read each remaining row and create a GameRecord.
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                // Make sure the row has every required column.
                if (parts.length <= largestRequiredColumn) {
                    throw new IOException(
                            "CSV row is missing one or more required values: " + line);
                }

                try {
                    String name = parts[nameCol].trim();

                    GameRecord.Continent continent =
                            GameRecord.Continent.valueOf(
                                    parts[continentCol].trim().toUpperCase());

                    int score = Integer.parseInt(parts[scoreCol].trim());
                    int health = Integer.parseInt(parts[healthCol].trim());
                    int collectables =
                            Integer.parseInt(parts[collectCol].trim());
                    String time = parts[timeCol].trim();

                    GameRecord record = new GameRecord(
                            name,
                            continent,
                            score,
                            health,
                            collectables,
                            time);

                    addRecord(record);

                } catch (IllegalArgumentException exception) {
                    throw new IOException(
                            "CSV row contains invalid data: " + line,
                            exception);
                }
            }
        }
    }

    /**
     * Sets the collectables range and returns all matching record names.
     *
     * @param low minimum collectables, or null for no minimum
     * @param high maximum collectables, or null for no maximum
     * @return names of records within the range and current filter
     */
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
        this.low = low;
        this.high = high;

        ArrayList<String> names = new ArrayList<>();

        for (GameRecord record : getMatchingRecords()) {
            names.add(record.getName());
        }

        return names;
    }

    /**
     * Sets the continent filter and returns all matching record names.
     *
     * @param continent continent to filter by, or null to clear the filter
     * @return names that match the filter and current range
     */
    @Override
    public List<String> applyAndSetFilter(
            GameRecord.Continent continent) {
        this.filter = continent;

        ArrayList<String> names = new ArrayList<>();

        for (GameRecord record : getMatchingRecords()) {
            names.add(record.getName());
        }

        return names;
    }

    /**
     * Returns up to ten records with the fastest completion times.
     *
     * @return names of the ten fastest matching records
     */
    @Override
    public List<String> getTopTen() {
        ArrayList<GameRecord> records = getMatchingRecords();

        Collections.sort(records, new Comparator<GameRecord>() {
            @Override
            public int compare(GameRecord first, GameRecord second) {
                return Integer.compare(
                        toSeconds(first.getCompletionTime()),
                        toSeconds(second.getCompletionTime()));
            }
        });

        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < records.size() && i < 10; i++) {
            names.add(records.get(i).getName());
        }

        return names;
    }

    /**
     * Finds records that satisfy the current range and continent filter.
     *
     * @return list of matching records
     */
    private ArrayList<GameRecord> getMatchingRecords() {
        if (low == null) {
            tree.setIteratorMin(null);
        } else {
            tree.setIteratorMin(makeBound(low));
        }

        if (high == null) {
            tree.setIteratorMax(null);
        } else {
            tree.setIteratorMax(makeBound(high));
        }

        ArrayList<GameRecord> matches = new ArrayList<>();

        for (GameRecord record : tree) {
            int collectables = record.getCollectables();

            if (low != null && collectables < low) {
                continue;
            }

            if (high != null && collectables > high) {
                continue;
            }

            if (filter != null && record.getContinent() != filter) {
                continue;
            }

            matches.add(record);
        }

        return matches;
    }

    /**
     * Creates a comparable boundary using a collectables value.
     *
     * @param collectables collectables value used as the boundary
     * @return comparable boundary for the tree iterator
     */
    private Comparable<GameRecord> makeBound(int collectables) {
        return new Comparable<GameRecord>() {
            @Override
            public int compareTo(GameRecord other) {
                return Integer.compare(
                        collectables,
                        other.getCollectables());
            }
        };
    }

    /**
     * Converts a completion time from hhh:mm:ss into seconds.
     *
     * @param time completion time formatted as hhh:mm:ss
     * @return completion time in total seconds
     */
    private int toSeconds(String time) {
        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return hours * 3600 + minutes * 60 + seconds;
    }
}