import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Backend implements BackendInterface {

    // the tree we store all the records in 
    private IterableSortedCollection<GameRecord> tree;

    // the collectables range we currently care about.
    private Integer low;
    private Integer high;

    // the continent we are filtering by
    private GameRecord.Continent filter;

    public Backend(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree;
        this.low = null;
        this.high = null;
        this.filter = null;
    }

    // add record into the tree
    public void addRecord(GameRecord record) {
        tree.insert(record);
    }

    public void readData(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(filename));

        // the first line is the header. figure out which column number each
     
        String[] headers = scanner.nextLine().split(",");
        int nameCol = -1;
        int continentCol = -1;
        int scoreCol = -1;
        int healthCol = -1;
        int collectCol = -1;
        int timeCol = -1;
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim();
            if (h.equals("name")) nameCol = i;
            else if (h.equals("continent")) continentCol = i;
            else if (h.equals("score")) scoreCol = i;
            else if (h.equals("max_health")) healthCol = i;
            else if (h.equals("collectables")) collectCol = i;
            else if (h.equals("completion_time")) timeCol = i;
            
        }

        // now go through every other line and turn it into a GameRecord
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");

            String name = parts[nameCol].trim();
            GameRecord.Continent continent = GameRecord.Continent.valueOf(parts[continentCol].trim());
            int score = Integer.parseInt(parts[scoreCol].trim());
            int health = Integer.parseInt(parts[healthCol].trim());
            int collectables = Integer.parseInt(parts[collectCol].trim());
            String time = parts[timeCol].trim();

            GameRecord record = new GameRecord(name, continent, score, health, collectables, time);
            addRecord(record);
        }
        scanner.close();
    }

    public List<String> getAndSetRange(Integer low, Integer high) {
        // save this range so the other methods can use it later
        this.low = low;
        this.high = high;

        ArrayList<String> names = new ArrayList<>();
        for (GameRecord r : getMatchingRecords()) {
            names.add(r.getName());
        }
        return names;
    }

    public List<String> applyAndSetFilter(GameRecord.Continent continent) {
        // save the filter if its null
        this.filter = continent;

        ArrayList<String> names = new ArrayList<>();
        for (GameRecord r : getMatchingRecords()) {
            names.add(r.getName());
        }
        return names;
    }

    public List<String> getTopTen() {
        ArrayList<GameRecord> records = getMatchingRecords();

    
        Collections.sort(records, new Comparator<GameRecord>() {
            public int compare(GameRecord a, GameRecord b) {
                return toSeconds(a.getCompletionTime()) - toSeconds(b.getCompletionTime());
            }
        });

        // grab the first 10
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < records.size() && i < 10; i++) {
            names.add(records.get(i).getName());
        }
        return names;
    }

    // in collectables order. all three of the methods above use this
    private ArrayList<GameRecord> getMatchingRecords() {
        
        if (low == null) tree.setIteratorMin(null);
        else tree.setIteratorMin(makeBound(low));
        if (high == null) tree.setIteratorMax(null);
        else tree.setIteratorMax(makeBound(high));

        ArrayList<GameRecord> matches = new ArrayList<>();
        for (GameRecord r : tree) {
            int c = r.getCollectables();
            // checking the range here too because the placeholder tree is buggy.
            // once the real tree works the lines above already handle this
            if (low != null && c < low) continue;
            if (high != null && c > high) continue;
            if (filter != null && r.getContinent() != filter) continue;
            matches.add(r);
        }
        return matches;
    }

    // setIteratorMin/Max want a Comparable, not just an int
    private Comparable<GameRecord> makeBound(int collectables) {
        return new Comparable<GameRecord>() {
            public int compareTo(GameRecord other) {
                return collectables - other.getCollectables();
            }
        };
    }

    // turns a "hhh:mm:ss" string into total seconds so times are easy to compare
    private int toSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }
}