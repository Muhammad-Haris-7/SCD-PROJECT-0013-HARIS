package football.ui;

import football.exception.DuplicateEntryException;
import football.exception.EntityNotFoundException;
import football.exception.ValidationException;
import football.model.Team;
import football.service.TeamService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing Teams — Add, Edit, Delete with a data table.
 */
public class TeamPanel extends JPanel {

    private final TeamService teamService;
    private final Runnable    onDataChanged;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        nameField, coachField, stadiumField;
    private JButton           addBtn, editBtn, deleteBtn, clearBtn;

    public TeamPanel(TeamService ts, Runnable onDataChanged) {
        this.teamService   = ts;
        this.onDataChanged = onDataChanged;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    // ── Form ─────────────────────────────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(4, 1, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Team Details"));

        nameField    = new JTextField();
        coachField   = new JTextField();
        stadiumField = new JTextField();

        form.add(UIHelper.labelledField("Team Name *",  nameField));
        form.add(UIHelper.labelledField("Coach *",      coachField));
        form.add(UIHelper.labelledField("Stadium *",    stadiumField));
        form.add(buildButtonRow());
        return form;
    }

    private JPanel buildButtonRow() {
        addBtn    = UIHelper.createButton("➕ Add Team",    UIHelper.BTN_ADD);
        editBtn   = UIHelper.createButton("✏️ Update",      UIHelper.BTN_EDIT);
        deleteBtn = UIHelper.createButton("🗑 Delete",      UIHelper.BTN_DELETE);
        clearBtn  = UIHelper.createButton("✖ Clear",       UIHelper.BTN_CANCEL);

        addBtn.addActionListener(e    -> handleAdd());
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e  -> clearForm());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.add(addBtn); row.add(editBtn); row.add(deleteBtn); row.add(clearBtn);
        return row;
    }

    // ── Table ────────────────────────────────────────────────────────────────

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID", "Team Name", "Coach", "Stadium", "MP", "W", "D", "L", "GF", "GA", "GD", "Pts"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        return new JScrollPane(table);
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    private void handleAdd() {
        try {
            teamService.addTeam(nameField.getText(), coachField.getText(), stadiumField.getText());
            UIHelper.showSuccess(this, "Team added successfully!");
            clearForm();
            onDataChanged.run();
        } catch (ValidationException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleEdit() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Please select a team to update."); return; }

        int id = (int) tableModel.getValueAt(row, 0);
        try {
            teamService.updateTeam(id, nameField.getText(), coachField.getText(), stadiumField.getText());
            UIHelper.showSuccess(this, "Team updated successfully!");
            clearForm();
            onDataChanged.run();
        } catch (ValidationException | EntityNotFoundException | DuplicateEntryException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void handleDelete() {
        int row = table.getSelectedRow();
        if (row < 0) { UIHelper.showError(this, "Please select a team to delete."); return; }

        String name = (String) tableModel.getValueAt(row, 1);
        if (!UIHelper.confirm(this, "Delete team '" + name + "'? This cannot be undone.")) return;

        int id = (int) tableModel.getValueAt(row, 0);
        try {
            teamService.deleteTeam(id);
            clearForm();
            onDataChanged.run();
        } catch (EntityNotFoundException ex) {
            UIHelper.showError(this, ex.getMessage());
        }
    }

    private void populateFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText((String) tableModel.getValueAt(row, 1));
        coachField.setText((String) tableModel.getValueAt(row, 2));
        stadiumField.setText((String) tableModel.getValueAt(row, 3));
    }

    private void clearForm() {
        nameField.setText("");
        coachField.setText("");
        stadiumField.setText("");
        table.clearSelection();
    }

    /** Reloads the table from service data */
    public void refresh() {
        tableModel.setRowCount(0);
        List<Team> teams = teamService.getAllTeams();
        for (Team t : teams) {
            tableModel.addRow(new Object[]{
                    t.getId(), t.getName(), t.getCoach(), t.getStadium(),
                    t.getMatchesPlayed(), t.getWins(), t.getDraws(), t.getLosses(),
                    t.getGoalsFor(), t.getGoalsAgainst(), t.getGoalDifference(), t.getPoints()
            });
        }
    }
}
