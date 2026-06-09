package football.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a football team.
 */
public class Team {

    private int id;
    private String name;
    private String coach;
    private String stadium;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private List<Player> players;

    public Team(int id, String name, String coach, String stadium) {
        this.id = id;
        this.name = name;
        this.coach = coach;
        this.stadium = stadium;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.players = new ArrayList<>();
    }

    // Getters
    public int getId()            { return id; }
    public String getName()       { return name; }
    public String getCoach()      { return coach; }
    public String getStadium()    { return stadium; }
    public int getWins()          { return wins; }
    public int getDraws()         { return draws; }
    public int getLosses()        { return losses; }
    public int getGoalsFor()      { return goalsFor; }
    public int getGoalsAgainst()  { return goalsAgainst; }
    public List<Player> getPlayers() { return players; }

    // Setters
    public void setName(String name)       { this.name = name; }
    public void setCoach(String coach)     { this.coach = coach; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public void setWins(int wins)          { this.wins = wins; }
    public void setDraws(int draws)        { this.draws = draws; }
    public void setLosses(int losses)      { this.losses = losses; }
    public void setGoalsFor(int g)         { this.goalsFor = g; }
    public void setGoalsAgainst(int g)     { this.goalsAgainst = g; }

    /** Points = 3 per win, 1 per draw */
    public int getPoints() {
        return (wins * 3) + draws;
    }

    /** Total matches played */
    public int getMatchesPlayed() {
        return wins + draws + losses;
    }

    /** Goal difference */
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    @Override
    public String toString() {
        return name + " | Coach: " + coach + " | Points: " + getPoints();
    }
}
