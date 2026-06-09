package football.util;

import football.exception.DuplicateEntryException;
import football.exception.ValidationException;
import football.model.Match;
import football.service.MatchService;
import football.service.PlayerService;
import football.service.TeamService;

import java.time.LocalDate;

/**
 * Loads sample teams, players, and matches so the app starts with meaningful data.
 */
public class SampleDataLoader {

    private SampleDataLoader() { /* Utility class */ }

    public static void load(TeamService teamService, PlayerService playerService, MatchService matchService) {
        try {
            // ── Teams ────────────────────────────────────────────────────────
            teamService.addTeam("Al-Hilal FC",     "Jorge Jesus",    "King Fahd Stadium");
            teamService.addTeam("Al-Nassr FC",     "Luis Castro",    "Al-Awwal Park");
            teamService.addTeam("Al-Ahli SC",      "Herve Renard",   "King Abdullah Stadium");
            teamService.addTeam("Al-Ittihad FC",   "Marcelo Gallardo","King Abdulaziz Stadium");
            teamService.addTeam("Peshawar FC",     "Ali Khan",       "Hayatabad Ground");
            teamService.addTeam("Lahore United",   "Hassan Raza",    "Gaddafi Stadium");

            // ── Players ──────────────────────────────────────────────────────
            playerService.addPlayer("Aleksandar Mitrovic", 30, "Striker",    "Al-Hilal FC");
            playerService.addPlayer("Kalidou Koulibaly",   33, "Defender",   "Al-Hilal FC");
            playerService.addPlayer("Cristiano Ronaldo",   39, "Forward",    "Al-Nassr FC");
            playerService.addPlayer("Anderson Talisca",    30, "Midfielder", "Al-Nassr FC");
            playerService.addPlayer("Riyad Mahrez",        33, "Winger",     "Al-Ahli SC");
            playerService.addPlayer("Roberto Firmino",     32, "Forward",    "Al-Ahli SC");
            playerService.addPlayer("N'Golo Kante",        33, "Midfielder", "Al-Ittihad FC");
            playerService.addPlayer("Karim Benzema",       36, "Striker",    "Al-Ittihad FC");
            playerService.addPlayer("Ahmed Nawaz",         25, "Goalkeeper", "Peshawar FC");
            playerService.addPlayer("Umar Hayat",          22, "Midfielder", "Lahore United");

            // ── Stats ────────────────────────────────────────────────────────
            playerService.updatePlayerStats(1, 18, 5, 3, 0);  // Mitrovic
            playerService.updatePlayerStats(3, 22, 8, 2, 0);  // Ronaldo
            playerService.updatePlayerStats(5,  9, 11, 4, 1); // Mahrez
            playerService.updatePlayerStats(7,  2, 9, 5, 0);  // Kante
            playerService.updatePlayerStats(8, 15, 7, 1, 0);  // Benzema
            playerService.updatePlayerStats(6, 11, 6, 2, 0);  // Firmino

            // ── Matches ──────────────────────────────────────────────────────
            Match m1 = matchService.scheduleMatch("Al-Hilal FC",   "Al-Nassr FC",   LocalDate.of(2025,3,15), "King Fahd Stadium");
            Match m2 = matchService.scheduleMatch("Al-Ahli SC",    "Al-Ittihad FC", LocalDate.of(2025,3,18), "King Abdullah Stadium");
            Match m3 = matchService.scheduleMatch("Peshawar FC",   "Lahore United", LocalDate.of(2025,3,20), "Hayatabad Ground");
            Match m4 = matchService.scheduleMatch("Al-Nassr FC",   "Al-Ahli SC",    LocalDate.of(2025,4,1),  "Al-Awwal Park");
            Match m5 = matchService.scheduleMatch("Al-Ittihad FC", "Al-Hilal FC",   LocalDate.of(2025,4,5),  "King Abdulaziz Stadium");
            Match m6 = matchService.scheduleMatch("Lahore United", "Peshawar FC",   LocalDate.of(2025,6,20), "Gaddafi Stadium");

            // Record past results
            matchService.recordResult(m1.getId(), 3, 1);
            matchService.recordResult(m2.getId(), 1, 1);
            matchService.recordResult(m3.getId(), 2, 0);
            matchService.recordResult(m4.getId(), 0, 2);
            matchService.recordResult(m5.getId(), 2, 3);

        } catch (ValidationException | DuplicateEntryException e) {
            System.err.println("Sample data loading error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading sample data: " + e.getMessage());
        }
    }
}
