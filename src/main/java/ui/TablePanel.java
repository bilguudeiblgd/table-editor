package ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import model.TableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TablePanel extends JPanel {
    private JTable table;
    private TableModel tableModel;
    private JTextField rowInputField;
    private JTextField columnInputField;
    private JButton addRowButton;
    private JButton removeRowButton;
    private JButton addColumnButton;
    private JButton removeColumnButton;

    public TablePanel() {
        setLayout(new BorderLayout());

        tableModel = new TableModel();
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Enable grid lines
        table.setShowGrid(true);

        // Set the grid color (optional)
        table.setGridColor(java.awt.Color.BLACK);

        rowInputField = new JTextField(20);
        columnInputField = new JTextField(20);
        addRowButton = new JButton("Add Row");
        removeRowButton = new JButton("Remove Selected Row");
        addColumnButton = new JButton("Add Column");
        removeColumnButton = new JButton("Remove Last Column");

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.add(rowInputField);
        rowPanel.add(addRowButton);
        rowPanel.add(removeRowButton);

        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));
        columnPanel.add(columnInputField);
        columnPanel.add(addColumnButton);
        columnPanel.add(removeColumnButton);

        controlPanel.add(rowPanel);
        controlPanel.add(columnPanel);

        add(controlPanel, BorderLayout.SOUTH);

        addRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = rowInputField.getText();
                if (!text.isEmpty()) {
                    tableModel.addRow(text);
                    rowInputField.setText("");
                }
            }
        });

        removeRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                }
            }
        });

        addColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = columnInputField.getText();
                if (!text.isEmpty()) {
                    tableModel.addColumn(text);
                    columnInputField.setText("");
                }
            }
        });

        removeColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeLastColumn();
            }
        });
    }
}
