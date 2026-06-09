package football;

import football.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point.
 * Launches the Football Management System on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
