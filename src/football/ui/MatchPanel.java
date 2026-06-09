package football.ui;

import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Match;
import football.service.MatchService;
import football.service.TeamService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for scheduling matches and recording results.
 */
public class MatchPanel extends JPanel {

    private final MatchService matchService;
    private final TeamService  teamService;
    private final Runnable     onDataChanged;

    // Schedule form
    private JComboBox<String> homeCombo, awayCombo;
    private JTextField        dateField, venueField;
    private JButton           scheduleBtn;

    // Result form
    private JTextField homeScoreField, awayScoreField;
    private JButton    recordBtn, cancelMatchBtn, deleteBtn;

    // Table
    private DefaultTableModel tableModel;
    private JTable            table;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MatchPanel(MatchService ms, TeamService ts, Runnable onDataChanged) {
        this.matchService  = ms;
        this.teamService   = ts;
        this.onDataChanged = onDataChanged;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        add(buildTopSection(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildTopSection() {
        JPanel top = new JPanel(new GridLayout(1, 2, 12, 0));

        // ── Schedule panel ────────────────────────────────────────────────────
        JPanel schedulePanel = new JPanel(new GridLayout(5, 1, 4, 4));
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Schedule a Match"));
        homeCombo  = new JComboBox<>();
        awayCombo  = new JComboBox<>();
        dateField  = new JTextField(LocalDate.now().format(DATE_FMT));
        venueField = new JTextField();
        scheduleBtn = UIHelper.createButton("📅 Schedule Match", UIHelper.BTN_ADD);
        scheduleBtn.addActionListener(e -> handleSchedule());
        schedulePanel.add(UIHelper.labelledField("Home Team *", homeCombo));
        schedulePanel.add(UIHelper.labelledField("Away Team *", awayCombo));
        schedulePanel.add(UIHelper.labelledField("Date (dd/MM/yyyy) *", dateField));
        schedulePanel.add(UIHelper.labelledField("Venue *", venueField));
        schedulePanel.add(scheduleBtn);

        // ── Result panel ──────────────────────────────────────────────────────
        JPanel resultPanel = new JPanel(new GridLayout(5, 1, 4, 4));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Record Result (select match above)"));
        homeScoreField = new JTextField("0");
        awayScoreField = new JTextField("0");
        recordBtn      = UIHelper.createButton("✅ Record Result", new Color(46, 139, 87));
        cancelMatchBtn = UIHelper.createButton("🚫 Cancel Match",  new Color(200, 120, 0));
        deleteBtn      = UIHelper.createButton("🗑 Delete Match",  UIHelper.BTN_DELETE);

        recordBtn.addActionListener(e      -> handleRecordResult());
        cancelMatchBtn.addActionListener(e -> handleCancelMatch());
        deleteBtn.addActionListener(e      -> handleDeleteMatch());

        resultPanel.add(UIHelper.labelledField("Home Score", homeScoreField));
        resultPanel.add(UIHelper.labelledField("Away Score", awayScoreField));
        resultPanel.add(recordBtn);
        resultPanel.add(cancelMatchBtn);
        resultPanel.add(deleteBtn);

        top.add(schedulePanel);
        top.add(resultPanel);
        return top;
    }

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID","Home Team","Score","Away Team","Date","Venue","Status","Result"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        return new JScrollPane(table);
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    private void handleSchedule() {
        String home  = getComboValue(homeCombo);
        String away  = getComboValue(awayCombo);
        String venue = venueField.getText();
        LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            UIHelper.showError(this, "Date must be in format dd/MM/yyyy (e.g. 15/09/2025).");
            return;
        }
        try {
            matchService.scheduleMatch(home, away, date, venue);
            UIHelper.showSuccess(this, "Match scheduled!");
            venueField.setText("");
            onDataChanged.run();
        } catch (ValidationException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleRecordResult() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a match from the table."); return; }
        int matchId = (int) tableModel.getValueAt(row, 0);
        try {
            int homeScore = Integer.parseInt(homeScoreField.getText().trim());
            int awayScore = Integer.parseInt(awayScoreField.getText().trim());
            matchService.recordResult(matchId, homeScore, awayScore);
            UIHelper.showSuccess(this, "Result recorded! Standings updated.");
            onDataChanged.run();
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Scores must be whole numbers.");
        } catch (EntityNotFoundException | ValidationException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleCancelMatch() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a match to cancel."); return; }
        if (!UIHelper.confirm(this, "Cancel this match?")) return;
        int matchId = (int) tableModel.getValueAt(row, 0);
        try {
            matchService.cancelMatch(matchId);
            onDataChanged.run();
        } catch (EntityNotFoundException | ValidationException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleDeleteMatch() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a match to delete."); return; }
        if (!UIHelper.confirm(this, "Permanently delete this match?")) return;
        int matchId = (int) tableModel.getValueAt(row, 0);
        try {
            matchService.deleteMatch(matchId);
            onDataChanged.run();
        } catch (EntityNotFoundException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private String getComboValue(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        return sel != null ? sel.toString() : "";
    }

    public void refresh() {
        // Refresh team combos
        homeCombo.removeAllItems();
        awayCombo.removeAllItems();
        teamService.getAllTeams().forEach(t -> {
            homeCombo.addItem(t.getName());
            awayCombo.addItem(t.getName());
        });

        // Refresh table
        tableModel.setRowCount(0);
        List<Match> matches = matchService.getAllMatches();
        for (Match m : matches) {
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getHomeTeamName(),
                    m.getResultDisplay(),
                    m.getAwayTeamName(),
                    m.getFormattedDate(),
                    m.getVenue(),
                    m.getStatus(),
                    m.getOutcome()
            });
        }
    }
}
