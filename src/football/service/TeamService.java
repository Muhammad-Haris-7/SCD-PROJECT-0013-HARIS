package football.service;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Team;
import football.util.InputValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Team-related operations.
 */
public class TeamService {

    private final List<Team> teams = new ArrayList<>();
    private int nextId = 1;

    /** Adds a new team after validation */
    public Team addTeam(String name, String coach, String stadium)
            throws ValidationException, DuplicateEntryException {

        InputValidator.validateName(name, "Team name");
        InputValidator.validateName(coach, "Coach name");
        InputValidator.validateName(stadium, "Stadium name");

        if (existsByName(name)) {
            throw new DuplicateEntryException("A team named '" + name + "' already exists.");
        }

        Team team = new Team(nextId++, name.trim(), coach.trim(), stadium.trim());
        teams.add(team);
        return team;
    }

    /** Updates an existing team's details */
    public void updateTeam(int id, String name, String coach, String stadium)
            throws ValidationException, EntityNotFoundException, DuplicateEntryException {

        Team team = findById(id);
        InputValidator.validateName(name, "Team name");
        InputValidator.validateName(coach, "Coach name");
        InputValidator.validateName(stadium, "Stadium name");

        Optional<Team> conflict = teams.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name.trim()) && t.getId() != id)
                .findFirst();
        if (conflict.isPresent()) {
            throw new DuplicateEntryException("Another team named '" + name + "' already exists.");
        }

        team.setName(name.trim());
        team.setCoach(coach.trim());
        team.setStadium(stadium.trim());
    }

    /** Deletes a team by ID */
    public void deleteTeam(int id) throws EntityNotFoundException {
        Team team = findById(id);
        teams.remove(team);
    }

    /** Returns all teams sorted by points descending (standings order) */
    public List<Team> getStandings() {
        return teams.stream()
                .sorted(Comparator.comparingInt(Team::getPoints).reversed()
                        .thenComparingInt(Team::getGoalDifference).reversed())
                .collect(Collectors.toList());
    }

    /** Returns all teams (unsorted) */
    public List<Team> getAllTeams() {
        return new ArrayList<>(teams);
    }

    /** Finds team by name (case-insensitive) */
    public Optional<Team> findByName(String name) {
        return teams.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    /** Finds team by ID, throws if not found */
    public Team findById(int id) throws EntityNotFoundException {
        return teams.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team with ID " + id + " not found."));
    }

    /** Applies match outcome to both team records */
    public void applyMatchResult(String homeTeamName, String awayTeamName,
                                 int homeScore, int awayScore) {

        Optional<Team> homeOpt = findByName(homeTeamName);
        Optional<Team> awayOpt = findByName(awayTeamName);

        if (homeOpt.isEmpty() || awayOpt.isEmpty()) return;

        Team home = homeOpt.get();
        Team away = awayOpt.get();

        home.setGoalsFor(home.getGoalsFor() + homeScore);
        home.setGoalsAgainst(home.getGoalsAgainst() + awayScore);
        away.setGoalsFor(away.getGoalsFor() + awayScore);
        away.setGoalsAgainst(away.getGoalsAgainst() + homeScore);

        if (homeScore > awayScore) {
            home.setWins(home.getWins() + 1);
            away.setLosses(away.getLosses() + 1);
        } else if (awayScore > homeScore) {
            away.setWins(away.getWins() + 1);
            home.setLosses(home.getLosses() + 1);
        } else {
            home.setDraws(home.getDraws() + 1);
            away.setDraws(away.getDraws() + 1);
        }
    }

    private boolean existsByName(String name) {
        return teams.stream().anyMatch(t -> t.getName().equalsIgnoreCase(name.trim()));
    }

    public int getTeamCount() {
        return teams.size();
    }
}
