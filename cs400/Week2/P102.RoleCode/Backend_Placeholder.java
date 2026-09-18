import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

/**
 * This is a placeholder for the fully working Backend that will be developed
 * by one of your teammates this week and then integrated with your role code
 * in a future week.  It is designed to help develop and test the functionality
 * of your own Frontend role code this week.  Note the limitations described
 * below.
 */
public class Backend_Placeholder implements BackendInterface {

    // Presumably this placeholder is using a placeholder tree that is itself
    // not fully functional.  The future working Backend will be making use of
    // a fully working tree.
    // This field is public to allow test code to access it.
    public IterableSortedCollection<GameRecord> tree;

    public Backend_Placeholder(IterableSortedCollection<GameRecord> tree) {
        this.tree = tree;
    }

    // when this method is called, the same record (not the parameter) is added to this backend
    @Override
    public void addRecord(GameRecord record) {
      tree.insert(new GameRecord("ne0nVandal", GameRecord.Continent.NORTH_AMERICA, 49723, 544, 140, "902:36:58"));
    } 
    
    // when this method is called, an extra record is added to this backend
    @Override
    public void readData(String filename) throws IOException {
      tree.insert(new GameRecord("speedRoyalty", GameRecord.Continent.EUROPE, 47403,534,120, "640:09:00"));
      tree.insert(new GameRecord("xXxgamer47xXx", GameRecord.Continent.ASIA, 48563,539,130, "956:48:46"));
      tree.insert(new GameRecord("v0idt3mp0", GameRecord.Continent.AFRICA, 42023,434,140, "634:06:42"));
      tree.insert(new GameRecord("voidR1fter", GameRecord.Continent.ANTARCTICA, 50883, 549, 150, "034:38:52"));    
    }

    // these filters should work on the limited list of hard-coded records in tree
    @Override
    public List<String> getAndSetRange(Integer low, Integer high) {
      String lowString = "demo record (low: " + low + ")";
      GameRecord lowRecord = null;
      if (low != null) {
        lowRecord = new GameRecord(lowString, GameRecord.Continent.AFRICA, low, low, low, "000:00:00");
      }

      String highString = "demo record (high: " + low + ")";
      GameRecord highRecord = null;
      if (high != null) {
        highRecord = new GameRecord(highString, GameRecord.Continent.SOUTH_AMERICA, high, high, high, "999:99:99");
      }

      tree.setIteratorMin(lowRecord);
      tree.setIteratorMax(highRecord);
      return getTopTen();

    }
    
    // filters are being completely ignored here, and the fastest ten records 
    // are really the ten only
    @Override
    public List<String> applyAndSetFilter(GameRecord.Continent continent) {
      return getTopTen();
    }
      
    @Override
    public List<String> getTopTen() {
      List<String> names = new ArrayList<>();
      for(GameRecord record : tree) {
          names.add(record.getName());
      }
      return names;

    }

}
