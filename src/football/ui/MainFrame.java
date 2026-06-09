package football.ui;

import football.service.MatchService;
import football.service.PlayerService;
import football.service.TeamService;
import football.util.SampleDataLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for the Football Management System.
 * Sets up the tabbed layout and initialises all services.
 */
public class MainFrame extends JFrame {

    // ── Services (shared across all panels) ──────────────────────────────────
    private final TeamService   teamService   = new TeamService();
    private final PlayerService playerService = new PlayerService();
    private final MatchService  matchService  = new MatchService(teamService);

    // ── Panels ────────────────────────────────────────────────────────────────
    private DashboardPanel  dashboardPanel;
    private TeamPanel       teamPanel;
    private PlayerPanel     playerPanel;
    private MatchPanel      matchPanel;
    private StandingsPanel  standingsPanel;

    public MainFrame() {
        super("⚽  Football Management System");
        configureWindow();
        buildMenuBar();
        buildTabs();
        SampleDataLoader.load(teamService, playerService, matchService);
        refreshAllPanels();
        setVisible(true);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        applyTheme();
    }

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* fall back to default */ }
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        refreshItem.addActionListener(e -> refreshAllPanels());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmAndExit());
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void buildTabs() {
        dashboardPanel  = new DashboardPanel(teamService, playerService, matchService);
        teamPanel       = new TeamPanel(teamService, this::refreshAllPanels);
        playerPanel     = new PlayerPanel(playerService, teamService, this::refreshAllPanels);
        matchPanel      = new MatchPanel(matchService, teamService, this::refreshAllPanels);
        standingsPanel  = new StandingsPanel(teamService, playerService);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("🏠  Dashboard",   dashboardPanel);
        tabs.addTab("🏟️  Teams",       teamPanel);
        tabs.addTab("👤  Players",     playerPanel);
        tabs.addTab("📅  Matches",     matchPanel);
        tabs.addTab("🏆  Standings",   standingsPanel);

        // Refresh stats tab when selected
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) dashboardPanel.refresh();
            if (idx == 4) standingsPanel.refresh();
        });

        add(tabs, BorderLayout.CENTER);
    }

    /** Broadcasts a refresh to all panels */
    public void refreshAllPanels() {
        dashboardPanel.refresh();
        teamPanel.refresh();
        playerPanel.refresh();
        matchPanel.refresh();
        standingsPanel.refresh();
    }

    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "⚽  Football Management System\n" +
                "Version 1.0\n\n" +
                "Semester Project — Software Construction & Development\n" +
                "Demonstrates: Event Handling, Exception Handling,\n" +
                "Code Refactoring, Unit Testing\n\n" +
                "Built with Java & Java Swing",
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
