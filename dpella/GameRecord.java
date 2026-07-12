public class GameRecord implements Comparable<GameRecord> {
  
  private String name;
  private GameRecord.Continent location;
  private int score; //calculated by 81*collectables + 70*maxHealth + 303
  private int maxHealth;
  private int collectables;
  private String completionTime; //formatted in hhh:mm:ss

  //constructor
  public GameRecord(String name, GameRecord.Continent location, int score,
                    int maxHealth, int collectables, String completionTime) {
    this.name = name;
    this.location = location;
    this.score = score;
    this.maxHealth = maxHealth;
    this.collectables = collectables;
    this.completionTime = completionTime;
  }


  //accessors
  public String getName() {return this.name;}
  public GameRecord.Continent getContinent(){return this.location;}
  public int getScore() {return this.score;}
  public int getMaxHealth() {return this.maxHealth;}
  public int getCollectables() {return this.collectables;}
  public String getCompletionTime() {return this.completionTime;}


  // comparisons are made using collectables, GameRecords with more collectables are considered bigger
  @Override
  public int compareTo(GameRecord other) {
    return this.collectables - other.collectables;
  }

  protected static enum Continent {
    AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA
  }
}
