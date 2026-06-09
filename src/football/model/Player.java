package football.model;

/**
 * Represents a football player in the system.
 */
public class Player {

    private int id;
    private String name;
    private int age;
    private String position;
    private String teamName;
    private int goalsScored;
    private int assists;
    private int yellowCards;
    private int redCards;

    public Player(int id, String name, int age, String position, String teamName) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
        this.teamName = teamName;
        this.goalsScored = 0;
        this.assists = 0;
        this.yellowCards = 0;
        this.redCards = 0;
    }

    // Getters
    public int getId()          { return id; }
    public String getName()     { return name; }
    public int getAge()         { return age; }
    public String getPosition() { return position; }
    public String getTeamName() { return teamName; }
    public int getGoalsScored() { return goalsScored; }
    public int getAssists()     { return assists; }
    public int getYellowCards() { return yellowCards; }
    public int getRedCards()    { return redCards; }

    // Setters
    public void setName(String name)         { this.name = name; }
    public void setAge(int age)              { this.age = age; }
    public void setPosition(String position) { this.position = position; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setGoalsScored(int goals)    { this.goalsScored = goals; }
    public void setAssists(int assists)      { this.assists = assists; }
    public void setYellowCards(int cards)    { this.yellowCards = cards; }
    public void setRedCards(int cards)       { this.redCards = cards; }

    /** Returns combined player rating score for display */
    public int getPerformanceScore() {
        return (goalsScored * 3) + (assists * 2) - (yellowCards) - (redCards * 3);
    }

    @Override
    public String toString() {
        return name + " | " + position + " | " + teamName + " | Goals: " + goalsScored;
    }
}
