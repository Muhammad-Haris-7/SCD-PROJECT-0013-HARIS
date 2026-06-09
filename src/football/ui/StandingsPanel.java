package football.ui;

import football.model.Player;
import football.model.Team;
import football.service.PlayerService;
import football.service.TeamService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Standings panel — league table and top scorer charts.
 */
public class StandingsPanel extends JPanel {

    private final TeamService   teamService;
    private final PlayerService playerService;

    private DefaultTableModel standingsModel;
    private DefaultTableModel scorersModel;

    public StandingsPanel(TeamService ts, PlayerService ps) {
        this.teamService   = ts;
        this.playerService = ps;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridLayout(2, 1, 10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildStandingsSection());
        add(buildTopScorersSection());
    }

    private JPanel buildStandingsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("🏆  League Standings"));

        String[] cols = {"#", "Team", "MP", "W", "D", "L", "GF", "GA", "GD", "Pts"};
        standingsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(standingsModel);
        t.setRowHeight(26);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        // Highlight top 3 rows
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    if (row == 0) c.setBackground(new Color(255, 223, 100));      // Gold
                    else if (row == 1) c.setBackground(new Color(210, 225, 240)); // Silver
                    else if (row == 2) c.setBackground(new Color(240, 210, 185)); // Bronze
                    else c.setBackground(Color.WHITE);
                }
                setHorizontalAlignment(col > 1 ? CENTER : LEFT);
                return c;
            }
        });

        t.getColumnModel().getColumn(0).setPreferredWidth(30);
        t.getColumnModel().getColumn(1).setPreferredWidth(180);

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTopScorersSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("🥅  Top Scorers & Stats"));

        String[] cols = {"#", "Player", "Team", "Position", "Goals", "Assists", "Yellow", "Red", "Score"};
        scorersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(scorersModel);
        t.setRowHeight(24);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.getColumnModel().getColumn(0).setPreferredWidth(30);
        t.getColumnModel().getColumn(1).setPreferredWidth(160);
        t.getColumnModel().getColumn(2).setPreferredWidth(140);

        panel.add(new JScrollPane(t), BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        // Standings
        standingsModel.setRowCount(0);
        List<Team> standings = teamService.getStandings();
        int pos = 1;
        for (Team t : standings) {
            standingsModel.addRow(new Object[]{
                    pos++, t.getName(), t.getMatchesPlayed(),
                    t.getWins(), t.getDraws(), t.getLosses(),
                    t.getGoalsFor(), t.getGoalsAgainst(), t.getGoalDifference(), t.getPoints()
            });
        }

        // Top scorers
        scorersModel.setRowCount(0);
        List<Player> topScorers = playerService.getTopScorers();
        int rank = 1;
        for (Player p : topScorers) {
            scorersModel.addRow(new Object[]{
                    rank++, p.getName(), p.getTeamName(), p.getPosition(),
                    p.getGoalsScored(), p.getAssists(),
                    p.getYellowCards(), p.getRedCards(), p.getPerformanceScore()
            });
        }
    }
}
