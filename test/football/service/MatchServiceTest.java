package football.service;

import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Match;
import football.model.Match.Status;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for MatchService.
 */
public class MatchServiceTest {

    private MatchService matchService;
    private TeamService  teamService;

    @Before
    public void setUp() throws Exception {
        teamService  = new TeamService();
        matchService = new MatchService(teamService);
        teamService.addTeam("Home FC", "Coach A", "Home Stadium");
        teamService.addTeam("Away FC", "Coach B", "Away Stadium");
    }

    // ── Schedule Match ────────────────────────────────────────────────────────

    @Test
    public void testScheduleMatch_success() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        assertNotNull(m);
        assertEquals("Home FC", m.getHomeTeamName());
        assertEquals("Away FC", m.getAwayTeamName());
        assertEquals(Status.SCHEDULED, m.getStatus());
    }

    @Test
    public void testScheduleMatch_incrementsCount() throws Exception {
        assertEquals(0, matchService.getMatchCount());
        matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        assertEquals(1, matchService.getMatchCount());
    }

    @Test(expected = ValidationException.class)
    public void testScheduleMatch_sameTeams_throwsValidationException() throws Exception {
        matchService.scheduleMatch("Home FC", "Home FC", LocalDate.now(), "Venue");
    }

    @Test(expected = ValidationException.class)
    public void testScheduleMatch_emptyVenue_throwsValidationException() throws Exception {
        matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "");
    }

    @Test(expected = ValidationException.class)
    public void testScheduleMatch_nullDate_throwsValidationException() throws Exception {
        matchService.scheduleMatch("Home FC", "Away FC", null, "Venue");
    }

    // ── Record Result ─────────────────────────────────────────────────────────

    @Test
    public void testRecordResult_success() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), 2, 1);
        Match updated = matchService.findById(m.getId());
        assertEquals(Status.COMPLETED, updated.getStatus());
        assertEquals(2, updated.getHomeScore());
        assertEquals(1, updated.getAwayScore());
    }

    @Test(expected = ValidationException.class)
    public void testRecordResult_alreadyCompleted_throwsValidationException() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), 1, 0);
        matchService.recordResult(m.getId(), 2, 0); // second attempt
    }

    @Test(expected = ValidationException.class)
    public void testRecordResult_negativeScore_throwsValidationException() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), -1, 0);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testRecordResult_unknownMatch_throwsEntityNotFoundException() throws Exception {
        matchService.recordResult(999, 1, 0);
    }

    // ── Cancel Match ──────────────────────────────────────────────────────────

    @Test
    public void testCancelMatch_success() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.cancelMatch(m.getId());
        assertEquals(Status.CANCELLED, matchService.findById(m.getId()).getStatus());
    }

    @Test(expected = ValidationException.class)
    public void testCancelMatch_alreadyCompleted_throwsValidationException() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), 1, 1);
        matchService.cancelMatch(m.getId()); // can't cancel completed
    }

    // ── Query Methods ─────────────────────────────────────────────────────────

    @Test
    public void testGetUpcomingMatches_returnsOnlyScheduled() throws Exception {
        Match m1 = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue 1");
        Match m2 = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now().plusDays(3), "Venue 2");
        matchService.recordResult(m1.getId(), 1, 0); // complete m1
        List<Match> upcoming = matchService.getUpcomingMatches();
        assertEquals(1, upcoming.size());
        assertEquals(m2.getId(), upcoming.get(0).getId());
    }

    @Test
    public void testGetCompletedMatches_returnsOnlyCompleted() throws Exception {
        Match m1 = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now().plusDays(7), "Venue 2");
        matchService.recordResult(m1.getId(), 0, 0);
        List<Match> completed = matchService.getCompletedMatches();
        assertEquals(1, completed.size());
    }

    // ── Result Display ────────────────────────────────────────────────────────

    @Test
    public void testGetResultDisplay_scheduledMatch_showsVs() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        assertEquals("vs", m.getResultDisplay());
    }

    @Test
    public void testGetResultDisplay_completedMatch_showsScore() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), 3, 2);
        assertEquals("3 - 2", m.getResultDisplay());
    }

    @Test
    public void testGetOutcome_draw() throws Exception {
        Match m = matchService.scheduleMatch("Home FC", "Away FC", LocalDate.now(), "Venue");
        matchService.recordResult(m.getId(), 1, 1);
        assertEquals("Draw", m.getOutcome());
    }
}
