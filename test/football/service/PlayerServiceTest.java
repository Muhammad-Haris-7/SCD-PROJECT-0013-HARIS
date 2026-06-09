package football.service;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for PlayerService.
 * Tests: add, update, delete, stats, validation, duplicate detection.
 */
public class PlayerServiceTest {

    private PlayerService service;

    @Before
    public void setUp() {
        service = new PlayerService();
    }

    // ── Add Player ────────────────────────────────────────────────────────────

    @Test
    public void testAddPlayer_success() throws Exception {
        Player p = service.addPlayer("Cristiano Ronaldo", 39, "Forward", "Al-Nassr FC");
        assertNotNull(p);
        assertEquals("Cristiano Ronaldo", p.getName());
        assertEquals(39, p.getAge());
        assertEquals("Forward", p.getPosition());
        assertEquals("Al-Nassr FC", p.getTeamName());
    }

    @Test
    public void testAddPlayer_incrementsCount() throws Exception {
        assertEquals(0, service.getPlayerCount());
        service.addPlayer("Player One", 25, "Midfielder", "Team A");
        service.addPlayer("Player Two", 22, "Defender", "Team B");
        assertEquals(2, service.getPlayerCount());
    }

    @Test(expected = DuplicateEntryException.class)
    public void testAddPlayer_duplicateName_throwsDuplicateEntryException() throws Exception {
        service.addPlayer("John Doe", 28, "Striker", "Team A");
        service.addPlayer("John Doe", 25, "Defender", "Team B"); // duplicate
    }

    @Test(expected = ValidationException.class)
    public void testAddPlayer_emptyName_throwsValidationException() throws Exception {
        service.addPlayer("", 25, "Striker", "Team A");
    }

    @Test(expected = ValidationException.class)
    public void testAddPlayer_ageTooYoung_throwsValidationException() throws Exception {
        service.addPlayer("Young Kid", 10, "Midfielder", "Team A");
    }

    @Test(expected = ValidationException.class)
    public void testAddPlayer_ageTooOld_throwsValidationException() throws Exception {
        service.addPlayer("Old Timer", 60, "Goalkeeper", "Team B");
    }

    @Test(expected = ValidationException.class)
    public void testAddPlayer_emptyTeam_throwsValidationException() throws Exception {
        service.addPlayer("Valid Name", 25, "Midfielder", "");
    }

    // ── Update Player ─────────────────────────────────────────────────────────

    @Test
    public void testUpdatePlayer_success() throws Exception {
        service.addPlayer("Old Name", 25, "Midfielder", "Team A");
        Player p = service.getAllPlayers().get(0);
        service.updatePlayer(p.getId(), "New Name", 26, "Defender", "Team B");
        Player updated = service.findById(p.getId());
        assertEquals("New Name", updated.getName());
        assertEquals(26, updated.getAge());
        assertEquals("Defender", updated.getPosition());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdatePlayer_notFound_throwsEntityNotFoundException() throws Exception {
        service.updatePlayer(999, "Name", 25, "Striker", "Team");
    }

    // ── Delete Player ─────────────────────────────────────────────────────────

    @Test
    public void testDeletePlayer_success() throws Exception {
        service.addPlayer("Delete Me", 28, "Forward", "Team A");
        Player p = service.getAllPlayers().get(0);
        service.deletePlayer(p.getId());
        assertEquals(0, service.getPlayerCount());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeletePlayer_notFound_throwsEntityNotFoundException() throws Exception {
        service.deletePlayer(999);
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    @Test
    public void testUpdateStats_success() throws Exception {
        service.addPlayer("Stats Player", 28, "Striker", "Team A");
        Player p = service.getAllPlayers().get(0);
        service.updatePlayerStats(p.getId(), 10, 5, 2, 0);
        Player updated = service.findById(p.getId());
        assertEquals(10, updated.getGoalsScored());
        assertEquals(5,  updated.getAssists());
        assertEquals(2,  updated.getYellowCards());
        assertEquals(0,  updated.getRedCards());
    }

    @Test(expected = ValidationException.class)
    public void testUpdateStats_negativeGoals_throwsValidationException() throws Exception {
        service.addPlayer("Player A", 25, "Forward", "Team A");
        Player p = service.getAllPlayers().get(0);
        service.updatePlayerStats(p.getId(), -1, 0, 0, 0);
    }

    @Test
    public void testPerformanceScore_calculatedCorrectly() throws Exception {
        service.addPlayer("Score Player", 27, "Striker", "Team A");
        Player p = service.getAllPlayers().get(0);
        service.updatePlayerStats(p.getId(), 5, 3, 1, 0);
        // Expected: (5*3) + (3*2) - 1 - 0 = 15 + 6 - 1 = 20
        assertEquals(20, p.getPerformanceScore());
    }

    // ── Query ─────────────────────────────────────────────────────────────────

    @Test
    public void testGetPlayersByTeam_returnsCorrectSubset() throws Exception {
        service.addPlayer("Player A", 25, "Striker",   "Team Alpha");
        service.addPlayer("Player B", 26, "Defender",  "Team Beta");
        service.addPlayer("Player C", 27, "Midfielder","Team Alpha");
        List<Player> alphaPlayers = service.getPlayersByTeam("Team Alpha");
        assertEquals(2, alphaPlayers.size());
    }

    @Test
    public void testGetTopScorers_sortedByGoalsDescending() throws Exception {
        service.addPlayer("Low Scorer",  25, "Forward", "A");
        service.addPlayer("High Scorer", 28, "Striker", "B");
        service.addPlayer("Mid Scorer",  22, "Winger",  "C");
        List<Player> players = service.getAllPlayers();
        service.updatePlayerStats(players.get(0).getId(), 3, 0, 0, 0);
        service.updatePlayerStats(players.get(1).getId(), 15, 0, 0, 0);
        service.updatePlayerStats(players.get(2).getId(), 8, 0, 0, 0);
        List<Player> top = service.getTopScorers();
        assertEquals("High Scorer", top.get(0).getName());
        assertEquals("Mid Scorer",  top.get(1).getName());
        assertEquals("Low Scorer",  top.get(2).getName());
    }
}
