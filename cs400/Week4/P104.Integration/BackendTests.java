import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
 
public class BackendTests {

 
    // 1 addRecord works and getAndSetRange gives back the right records,
    
    @Test
    public void roleTest1() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);
 
        // before adding anything we should just get the 3 built in records
        List<String> all = backend.getAndSetRange(null, null);
        assertEquals(3, all.size());
        assertTrue(all.contains("speedRoyalty"));
        assertTrue(all.contains("xXxgamer47xXx"));
        assertTrue(all.contains("v0idt3mp0"));
 
        
        backend.addRecord(new GameRecord("myTest", GameRecord.Continent.EUROPE, 0, 0, 135, "010:00:00"));
        List<String> afterAdd = backend.getAndSetRange(null, null);
        assertEquals(4, afterAdd.size());
        assertTrue(afterAdd.contains("myTest"));
 
        
        List<String> ranged = backend.getAndSetRange(125, null);
        assertFalse(ranged.contains("speedRoyalty"));
        assertTrue(ranged.contains("xXxgamer47xXx")); // 130
        assertTrue(ranged.contains("v0idt3mp0"));      // 140
    }
 
    // test 2: filtering by continent, and making sure null clears the filter
    @Test
    public void roleTest2() {
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);
 
        // filter to eu
        backend.getAndSetRange(null, null);
        List<String> euro = backend.applyAndSetFilter(GameRecord.Continent.EUROPE);
        assertEquals(1, euro.size());
        assertEquals("speedRoyalty", euro.get(0));
 
        // filter to  asia
        List<String> asia = backend.applyAndSetFilter(GameRecord.Continent.ASIA);
        assertEquals(1, asia.size());
        assertEquals("xXxgamer47xXx", asia.get(0));
 
        // passing null clears the filter so we get all 3 back
        List<String> cleared = backend.applyAndSetFilter(null);
        assertEquals(3, cleared.size());
    }
 
    // test 3 getTopTen sorts by fastest time
    @Test
    public void roleTest3() throws IOException {
       
        Tree_Placeholder tree = new Tree_Placeholder();
        Backend backend = new Backend(tree);
        List<String> top = backend.getTopTen();
        assertEquals("v0idt3mp0", top.get(0));
        assertEquals("speedRoyalty", top.get(1));
        assertEquals("xXxgamer47xXx", top.get(2));
 
        // make a tiny csv to test readData. headers are out of order on purpose
    
        File csv = File.createTempFile("records", ".csv");
        csv.deleteOnExit();
        FileWriter writer = new FileWriter(csv);
        writer.write("collectables,name,level,continent,completion_time,score,max_health\n");
        writer.write("99,loadedRecord,777,ASIA,012:34:56,5000,250\n");
        writer.close();
 
        Tree_Placeholder tree2 = new Tree_Placeholder();
        Backend backend2 = new Backend(tree2);
        backend2.readData(csv.getPath());
 
        // the placeholder only keeps the LAST record it was given, and it's
    
        assertEquals("loadedRecord", tree2.lastAddedGameRecord.getName());
        assertEquals(99, tree2.lastAddedGameRecord.getCollectables());
        assertEquals(GameRecord.Continent.ASIA, tree2.lastAddedGameRecord.getContinent());
    }
}