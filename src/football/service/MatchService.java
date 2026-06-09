package football.service;

import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Match;
import football.model.Match.Status;
import football.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Match scheduling and result management.
 */
public class MatchService {

    private final List<Match> matches = new ArrayList<>();
    private final TeamService teamService;
    private int nextId = 1;

    public MatchService(TeamService teamService) {
        this.teamService = teamService;
    }

    /** Schedules a new match */
    public Match scheduleMatch(String homeTeam, String awayTeam, LocalDate date, String venue)
            throws ValidationException {

        InputValidator.validateName(homeTeam, "Home team");
        InputValidator.validateName(awayTeam, "Away team");
        InputValidator.validateName(venue, "Venue");

        if (homeTeam.trim().equalsIgnoreCase(awayTeam.trim())) {
            throw new ValidationException("Home and away teams cannot be the same.");
        }
        if (date == null) {
            throw new ValidationException("Match date is required.");
        }

        Match match = new Match(nextId++, homeTeam.trim(), awayTeam.trim(), date, venue.trim());
        matches.add(match);
        return match;
    }

    /** Records the result of a match and updates team standings */
    public void recordResult(int matchId, int homeScore, int awayScore)
            throws EntityNotFoundException, ValidationException {

        Match match = findById(matchId);

        if (match.getStatus() == Status.COMPLETED) {
            throw new ValidationException("Result for this match has already been recorded.");
        }

        InputValidator.validateScore(homeScore);
        InputValidator.validateScore(awayScore);

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        match.setStatus(Status.COMPLETED);

        // Update team standings in TeamService
        teamService.applyMatchResult(match.getHomeTeamName(), match.getAwayTeamName(),
                homeScore, awayScore);
    }

    /** Cancels a scheduled match */
    public void cancelMatch(int matchId) throws EntityNotFoundException, ValidationException {
        Match match = findById(matchId);
        if (match.getStatus() == Status.COMPLETED) {
            throw new ValidationException("Cannot cancel a match that has already been completed.");
        }
        match.setStatus(Status.CANCELLED);
    }

    /** Deletes a match by ID */
    public void deleteMatch(int matchId) throws EntityNotFoundException {
        Match match = findById(matchId);
        matches.remove(match);
    }

    /** Returns all matches sorted by date */
    public List<Match> getAllMatches() {
        return matches.stream()
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    /** Returns only upcoming (scheduled) matches */
    public List<Match> getUpcomingMatches() {
        return matches.stream()
                .filter(m -> m.getStatus() == Status.SCHEDULED)
                .sorted(Comparator.comparing(Match::getMatchDate))
                .collect(Collectors.toList());
    }

    /** Returns only completed matches */
    public List<Match> getCompletedMatches() {
        return matches.stream()
                .filter(m -> m.getStatus() == Status.COMPLETED)
                .sorted(Comparator.comparing(Match::getMatchDate).reversed())
                .collect(Collectors.toList());
    }

    /** Finds a match by ID */
    public Match findById(int id) throws EntityNotFoundException {
        return matches.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Match with ID " + id + " not found."));
    }

    public int getMatchCount() { return matches.size(); }
    public long getCompletedCount() { return matches.stream().filter(m -> m.getStatus() == Status.COMPLETED).count(); }
}
