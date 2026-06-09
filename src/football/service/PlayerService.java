package football.service;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Player;
import football.util.InputValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for all Player-related operations.
 * Handles business logic and delegates validation to InputValidator.
 */
public class PlayerService {

    private final List<Player> players = new ArrayList<>();
    private int nextId = 1;

    /** Adds a new player after validation */
    public Player addPlayer(String name, int age, String position, String teamName)
            throws ValidationException, DuplicateEntryException {

        InputValidator.validateName(name, "Player name");
        InputValidator.validateAge(age);
        InputValidator.validateName(position, "Position");
        InputValidator.validateName(teamName, "Team name");

        if (existsByName(name)) {
            throw new DuplicateEntryException("A player named '" + name + "' already exists.");
        }

        Player player = new Player(nextId++, name.trim(), age, position.trim(), teamName.trim());
        players.add(player);
        return player;
    }

    /** Updates an existing player's details */
    public void updatePlayer(int id, String name, int age, String position, String teamName)
            throws ValidationException, EntityNotFoundException, DuplicateEntryException {

        Player player = findById(id);
        InputValidator.validateName(name, "Player name");
        InputValidator.validateAge(age);
        InputValidator.validateName(position, "Position");
        InputValidator.validateName(teamName, "Team name");

        // Allow same name if it belongs to the same player
        Optional<Player> conflict = players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name.trim()) && p.getId() != id)
                .findFirst();
        if (conflict.isPresent()) {
            throw new DuplicateEntryException("Another player named '" + name + "' already exists.");
        }

        player.setName(name.trim());
        player.setAge(age);
        player.setPosition(position.trim());
        player.setTeamName(teamName.trim());
    }

    /** Deletes a player by ID */
    public void deletePlayer(int id) throws EntityNotFoundException {
        Player player = findById(id);
        players.remove(player);
    }

    /** Updates a player's stats */
    public void updatePlayerStats(int id, int goals, int assists, int yellow, int red)
            throws EntityNotFoundException, ValidationException {

        Player player = findById(id);
        InputValidator.validateStat(goals, "Goals");
        InputValidator.validateStat(assists, "Assists");
        InputValidator.validateStat(yellow, "Yellow cards");
        InputValidator.validateStat(red, "Red cards");

        player.setGoalsScored(goals);
        player.setAssists(assists);
        player.setYellowCards(yellow);
        player.setRedCards(red);
    }

    /** Returns all players */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    /** Returns players belonging to a specific team */
    public List<Player> getPlayersByTeam(String teamName) {
        return players.stream()
                .filter(p -> p.getTeamName().equalsIgnoreCase(teamName))
                .collect(Collectors.toList());
    }

    /** Returns top scorers sorted by goals descending */
    public List<Player> getTopScorers() {
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getGoalsScored).reversed())
                .collect(Collectors.toList());
    }

    /** Finds player by ID, throws if not found */
    public Player findById(int id) throws EntityNotFoundException {
        return players.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Player with ID " + id + " not found."));
    }

    private boolean existsByName(String name) {
        return players.stream().anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()));
    }

    public int getPlayerCount() {
        return players.size();
    }
}
