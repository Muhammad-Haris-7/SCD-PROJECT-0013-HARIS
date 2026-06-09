package football.ui;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Player;
import football.model.Team;
import football.service.PlayerService;
import football.service.TeamService;
import football.util.InputValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing Players — Add, Edit, Delete, and update stats.
 */
public class PlayerPanel extends JPanel {

    private final PlayerService playerService;
    private final TeamService   teamService;
    private final Runnable      onDataChanged;

    // Form fields
    private JTextField  nameField, ageField, goalsField, assistsField, yellowField, redField;
    private JComboBox<String> positionCombo, teamCombo;
    private JButton     addBtn, editBtn, deleteBtn, updateStatsBtn, clearBtn;

    // Table
    private DefaultTableModel tableModel;
    private JTable            table;

    private static final String[] POSITIONS = {
        "Goalkeeper","Defender","Midfielder","Forward","Winger","Striker"
    };

    public PlayerPanel(PlayerService ps, TeamService ts, Runnable onDataChanged) {
        this.playerService = ps;
        this.teamService   = ts;
        this.onDataChanged = onDataChanged;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        add(buildFormPanel(),  BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 12, 0));
        wrapper.setBorder(BorderFactory.createTitledBorder("Player Details"));

        // Left column: basic info
        JPanel left = new JPanel(new GridLayout(5, 1, 4, 4));
        left.setOpaque(false);
        nameField     = new JTextField();
        ageField      = new JTextField();
        positionCombo = new JComboBox<>(POSITIONS);
        teamCombo     = new JComboBox<>();
        left.add(UIHelper.labelledField("Name *",     nameField));
        left.add(UIHelper.labelledField("Age *",      ageField));
        left.add(UIHelper.labelledField("Position *", positionCombo));
        left.add(UIHelper.labelledField("Team *",     teamCombo));
        left.add(buildButtonRow());

        // Right column: stats
        JPanel right = new JPanel(new GridLayout(5, 1, 4, 4));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        goalsField   = new JTextField("0");
        assistsField = new JTextField("0");
        yellowField  = new JTextField("0");
        redField     = new JTextField("0");
        right.add(UIHelper.labelledField("Goals",        goalsField));
        right.add(UIHelper.labelledField("Assists",      assistsField));
        right.add(UIHelper.labelledField("Yellow Cards", yellowField));
        right.add(UIHelper.labelledField("Red Cards",    redField));
        updateStatsBtn = UIHelper.createButton("💾 Save Stats", UIHelper.BTN_SAVE);
        updateStatsBtn.addActionListener(e -> handleUpdateStats());
        right.add(updateStatsBtn);

        wrapper.add(left);
        wrapper.add(right);
        return wrapper;
    }

    private JPanel buildButtonRow() {
        addBtn    = UIHelper.createButton("➕ Add",    UIHelper.BTN_ADD);
        editBtn   = UIHelper.createButton("✏️ Update", UIHelper.BTN_EDIT);
        deleteBtn = UIHelper.createButton("🗑 Delete", UIHelper.BTN_DELETE);
        clearBtn  = UIHelper.createButton("✖ Clear",  UIHelper.BTN_CANCEL);

        addBtn.addActionListener(e    -> handleAdd());
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e  -> clearForm());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        row.add(addBtn); row.add(editBtn); row.add(deleteBtn); row.add(clearBtn);
        return row;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID","Name","Age","Position","Team","Goals","Assists","Yellow","Red","Score"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        return new JScrollPane(table);
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            int age = InputValidator.parseIntField(ageField.getText(), "Age");
            String team = getSelectedTeam();
            playerService.addPlayer(nameField.getText(), age,
                    (String) positionCombo.getSelectedItem(), team);
            UIHelper.showSuccess(this, "Player added successfully!");
            clearForm();
            onDataChanged.run();
        } catch (ValidationException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Please select a player to update."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            int age = InputValidator.parseIntField(ageField.getText(), "Age");
            playerService.updatePlayer(id, nameField.getText(), age,
                    (String) positionCombo.getSelectedItem(), getSelectedTeam());
            UIHelper.showSuccess(this, "Player updated!");
            clearForm();
            onDataChanged.run();
        } catch (ValidationException | EntityNotFoundException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Please select a player to delete."); return; }
        String name = (String) tableModel.getValueAt(row, 1);
        if (!UIHelper.confirm(this, "Delete player '" + name + "'?")) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            playerService.deletePlayer(id);
            clearForm();
            onDataChanged.run();
        } catch (EntityNotFoundException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleUpdateStats() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Select a player first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            int goals   = InputValidator.parseIntField(goalsField.getText(),   "Goals");
            int assists = InputValidator.parseIntField(assistsField.getText(), "Assists");
            int yellow  = InputValidator.parseIntField(yellowField.getText(),  "Yellow cards");
            int red     = InputValidator.parseIntField(redField.getText(),     "Red cards");
            playerService.updatePlayerStats(id, goals, assists, yellow, red);
            UIHelper.showSuccess(this, "Stats updated!");
            onDataChanged.run();
        } catch (ValidationException | EntityNotFoundException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String) tableModel.getValueAt(row, 1));
        ageField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        positionCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        teamCombo.setSelectedItem(tableModel.getValueAt(row, 4));
        goalsField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        assistsField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        yellowField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        redField.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }

    private String getSelectedTeam() {
        Object sel = teamCombo.getSelectedItem();
        return sel != null ? sel.toString() : "";
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        goalsField.setText("0");
        assistsField.setText("0");
        yellowField.setText("0");
        redField.setText("0");
        positionCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    public void refresh() {
        // Refresh team combo
        String prevTeam = getSelectedTeam();
        teamCombo.removeAllItems();
        teamService.getAllTeams().forEach(t -> teamCombo.addItem(t.getName()));
        if (prevTeam != null && !prevTeam.isEmpty()) teamCombo.setSelectedItem(prevTeam);

        // Refresh table
        tableModel.setRowCount(0);
        for (Player p : playerService.getAllPlayers()) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(), p.getPosition(), p.getTeamName(),
                    p.getGoalsScored(), p.getAssists(), p.getYellowCards(), p.getRedCards(),
                    p.getPerformanceScore()
            });
        }
    }
}
