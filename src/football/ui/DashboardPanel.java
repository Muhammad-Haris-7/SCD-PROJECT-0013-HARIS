package football.ui;

import football.model.Match;
import football.model.Player;
import football.model.Team;
import football.service.MatchService;
import football.service.PlayerService;
import football.service.TeamService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel — overview of key statistics.
 */
public class DashboardPanel extends JPanel {

    private final TeamService   teamService;
    private final PlayerService playerService;
    private final MatchService  matchService;

    private JLabel teamsCountLabel;
    private JLabel playersCountLabel;
    private JLabel matchesCountLabel;
    private JLabel completedCountLabel;
    private JTextArea recentResultsArea;
    private JTextArea topScorersArea;

    public DashboardPanel(TeamService ts, PlayerService ps, MatchService ms) {
        this.teamService   = ts;
        this.playerService = ps;
        this.matchService  = ms;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        // Title
        JLabel title = new JLabel("⚽  Football Management System — Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 80, 160));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Stats cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);
        teamsCountLabel    = new JLabel("0", SwingConstants.CENTER);
        playersCountLabel  = new JLabel("0", SwingConstants.CENTER);
        matchesCountLabel  = new JLabel("0", SwingConstants.CENTER);
        completedCountLabel= new JLabel("0", SwingConstants.CENTER);

        statsRow.add(createStatCard("Teams",           teamsCountLabel,    new Color(52, 152, 219)));
        statsRow.add(createStatCard("Players",         playersCountLabel,  new Color(46, 204, 113)));
        statsRow.add(createStatCard("Matches Scheduled", matchesCountLabel, new Color(230, 126, 34)));
        statsRow.add(createStatCard("Results Recorded",completedCountLabel,new Color(155, 89, 182)));

        // Bottom panels
        recentResultsArea = createTextArea();
        topScorersArea    = createTextArea();

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 12, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(createScrollSection("📋  Recent Results", recentResultsArea));
        bottomRow.add(createScrollSection("🥅  Top Scorers", topScorersArea));

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(statsRow, BorderLayout.NORTH);
        center.add(bottomRow, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(14, 10, 14, 10)));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(new Color(230, 240, 255));

        card.add(valueLabel);
        card.add(lbl);
        return card;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(6, 8, 6, 8));
        return area;
    }

    private JPanel createScrollSection(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                title, 0, 0,
                new Font("SansSerif", Font.BOLD, 13)));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    /** Refreshes all dashboard data from services */
    public void refresh() {
        teamsCountLabel.setText(String.valueOf(teamService.getTeamCount()));
        playersCountLabel.setText(String.valueOf(playerService.getPlayerCount()));
        matchesCountLabel.setText(String.valueOf(matchService.getMatchCount()));
        completedCountLabel.setText(String.valueOf(matchService.getCompletedCount()));

        // Recent results
        List<Match> completed = matchService.getCompletedMatches();
        StringBuilder sb = new StringBuilder();
        int count = Math.min(completed.size(), 8);
        for (int i = 0; i < count; i++) {
            Match m = completed.get(i);
            sb.append(String.format("%-18s %s %-18s  [%s]%n",
                    m.getHomeTeamName(), m.getResultDisplay(),
                    m.getAwayTeamName(), m.getFormattedDate()));
        }
        recentResultsArea.setText(sb.isEmpty() ? "No results recorded yet." : sb.toString());

        // Top scorers
        List<Player> scorers = playerService.getTopScorers();
        StringBuilder sb2 = new StringBuilder();
        int rank = 1;
        for (Player p : scorers) {
            if (rank > 8) break;
            sb2.append(String.format("%d. %-20s  Goals: %d  Assists: %d%n",
                    rank++, p.getName(), p.getGoalsScored(), p.getAssists()));
        }
        topScorersArea.setText(sb2.isEmpty() ? "No player stats recorded yet." : sb2.toString());
    }
}
