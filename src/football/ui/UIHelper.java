package football.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Shared UI helper methods to eliminate duplication across panels.
 */
public class UIHelper {

    private UIHelper() { /* Utility class */ }

    /** Shows an error dialog */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows an info/success dialog */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows a yes/no confirmation; returns true if confirmed */
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Creates a standard labelled text field row panel */
    public static JPanel labelledField(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 24));
        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** Creates a standard action button */
    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return btn;
    }

    /** Standard colours */
    public static final Color BTN_ADD    = new Color(46, 139, 87);
    public static final Color BTN_EDIT   = new Color(30, 100, 200);
    public static final Color BTN_DELETE = new Color(192, 57, 43);
    public static final Color BTN_SAVE   = new Color(52, 152, 219);
    public static final Color BTN_CANCEL = new Color(127, 140, 141);
}
