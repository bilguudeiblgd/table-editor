package ui;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Table Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new TablePanel());

        setVisible(true);
    }
}
