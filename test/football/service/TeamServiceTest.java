package football.service;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Team;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for TeamService.
 */
public class TeamServiceTest {

    private TeamService service;

    @Before
    public void setUp() {
        service = new TeamService();
    }

    // ── Add Team ──────────────────────────────────────────────────────────────

    @Test
    public void testAddTeam_success() throws Exception {
        Team t = service.addTeam("Arsenal FC", "Mikel Arteta", "Emirates Stadium");
        assertNotNull(t);
        assertEquals("Arsenal FC", t.getName());
        assertEquals("Mikel Arteta", t.getCoach());
        assertEquals("Emirates Stadium", t.getStadium());
    }

    @Test
    public void testAddTeam_initialStatsAreZero() throws Exception {
        Team t = service.addTeam("New Team", "Coach Name", "Stadium");
        assertEquals(0, t.getWins());
        assertEquals(0, t.getDraws());
        assertEquals(0, t.getLosses());
        assertEquals(0, t.getPoints());
        assertEquals(0, t.getMatchesPlayed());
    }

    @Test(expected = DuplicateEntryException.class)
    public void testAddTeam_duplicate_throwsDuplicateEntryException() throws Exception {
        service.addTeam("Team A", "Coach 1", "Stadium 1");
        service.addTeam("Team A", "Coach 2", "Stadium 2");
    }

    @Test(expected = ValidationException.class)
    public void testAddTeam_emptyName_throwsValidationException() throws Exception {
        service.addTeam("", "Coach", "Stadium");
    }

    @Test(expected = ValidationException.class)
    public void testAddTeam_emptyCoach_throwsValidationException() throws Exception {
        service.addTeam("Valid Team", "", "Stadium");
    }

    // ── Update Team ───────────────────────────────────────────────────────────

    @Test
    public void testUpdateTeam_success() throws Exception {
        service.addTeam("Old Name", "Old Coach", "Old Stadium");
        Team t = service.getAllTeams().get(0);
        service.updateTeam(t.getId(), "New Name", "New Coach", "New Stadium");
        Team updated = service.findById(t.getId());
        assertEquals("New Name", updated.getName());
        assertEquals("New Coach", updated.getCoach());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateTeam_notFound_throwsEntityNotFoundException() throws Exception {
        service.updateTeam(999, "Name", "Coach", "Stadium");
    }

    // ── Delete Team ───────────────────────────────────────────────────────────

    @Test
    public void testDeleteTeam_success() throws Exception {
        service.addTeam("Delete Team", "Coach", "Stadium");
        Team t = service.getAllTeams().get(0);
        service.deleteTeam(t.getId());
        assertEquals(0, service.getTeamCount());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteTeam_notFound_throwsEntityNotFoundException() throws Exception {
        service.deleteTeam(999);
    }

    // ── Match Result & Standings ──────────────────────────────────────────────

    @Test
    public void testApplyMatchResult_homeWin_updatesRecordsCorrectly() throws Exception {
        service.addTeam("Home Team", "Coach A", "Stadium A");
        service.addTeam("Away Team", "Coach B", "Stadium B");
        service.applyMatchResult("Home Team", "Away Team", 3, 1);

        Team home = service.findByName("Home Team").orElseThrow();
        Team away = service.findByName("Away Team").orElseThrow();

        assertEquals(1, home.getWins());
        assertEquals(0, home.getLosses());
        assertEquals(3, home.getPoints());
        assertEquals(0, away.getWins());
        assertEquals(1, away.getLosses());
        assertEquals(0, away.getPoints());
    }

    @Test
    public void testApplyMatchResult_draw_updatesRecordsCorrectly() throws Exception {
        service.addTeam("Team X", "Coach X", "Stadium X");
        service.addTeam("Team Y", "Coach Y", "Stadium Y");
        service.applyMatchResult("Team X", "Team Y", 2, 2);

        Team x = service.findByName("Team X").orElseThrow();
        Team y = service.findByName("Team Y").orElseThrow();

        assertEquals(1, x.getDraws());
        assertEquals(1, y.getDraws());
        assertEquals(1, x.getPoints());
        assertEquals(1, y.getPoints());
    }

    @Test
    public void testGetStandings_sortedByPointsThenGoalDifference() throws Exception {
        service.addTeam("Top",    "C1", "S1");
        service.addTeam("Middle", "C2", "S2");
        service.addTeam("Bottom", "C3", "S3");
        service.applyMatchResult("Top", "Middle", 3, 0);
        service.applyMatchResult("Top", "Bottom", 2, 0);
        List<Team> standings = service.getStandings();
        assertEquals("Top", standings.get(0).getName());
    }

    @Test
    public void testGoalDifference_calculatedCorrectly() throws Exception {
        service.addTeam("GD Team", "Coach", "Stadium");
        service.addTeam("Other",   "Coach", "Stadium");
        service.applyMatchResult("GD Team", "Other", 4, 1);
        Team t = service.findByName("GD Team").orElseThrow();
        assertEquals(3, t.getGoalDifference()); // 4 - 1
    }
}
