package football.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a football match between two teams.
 */
public class Match {

    public enum Status { SCHEDULED, COMPLETED, CANCELLED }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDate matchDate;
    private String venue;
    private int homeScore;
    private int awayScore;
    private Status status;

    public Match(int id, String homeTeamName, String awayTeamName, LocalDate matchDate, String venue) {
        this.id = id;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.matchDate = matchDate;
        this.venue = venue;
        this.homeScore = 0;
        this.awayScore = 0;
        this.status = Status.SCHEDULED;
    }

    // Getters
    public int getId()              { return id; }
    public String getHomeTeamName() { return homeTeamName; }
    public String getAwayTeamName() { return awayTeamName; }
    public LocalDate getMatchDate() { return matchDate; }
    public String getVenue()        { return venue; }
    public int getHomeScore()       { return homeScore; }
    public int getAwayScore()       { return awayScore; }
    public Status getStatus()       { return status; }

    // Setters
    public void setHomeScore(int score) { this.homeScore = score; }
    public void setAwayScore(int score) { this.awayScore = score; }
    public void setStatus(Status status){ this.status = status; }
    public void setMatchDate(LocalDate d) { this.matchDate = d; }
    public void setVenue(String venue)  { this.venue = venue; }

    /** Returns result string like "3-1" or "vs" if not played */
    public String getResultDisplay() {
        if (status == Status.COMPLETED) {
            return homeScore + " - " + awayScore;
        }
        return "vs";
    }

    /** Returns outcome label from home team perspective */
    public String getOutcome() {
        if (status != Status.COMPLETED) return "Pending";
        if (homeScore > awayScore) return homeTeamName + " Win";
        if (awayScore > homeScore) return awayTeamName + " Win";
        return "Draw";
    }

    public String getFormattedDate() {
        return matchDate.format(DATE_FORMAT);
    }

    @Override
    public String toString() {
        return homeTeamName + " " + getResultDisplay() + " " + awayTeamName
                + " | " + getFormattedDate() + " | " + status;
    }
}
