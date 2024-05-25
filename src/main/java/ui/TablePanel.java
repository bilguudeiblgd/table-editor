package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import model.TableModel;

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

        // Customize table header to add button when hovered
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setDefaultRenderer(new HeaderRenderer(table));
        tableHeader.addMouseListener(new HeaderMouseListener());
    }

    private class HeaderRenderer extends DefaultTableCellRenderer {
        private final JTable table;
        private int hoveredColumn = -1;

        public HeaderRenderer(JTable table) {
            this.table = table;
            table.getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int col = table.columnAtPoint(e.getPoint());
                    if (col != hoveredColumn) {
                        hoveredColumn = col;
                        table.getTableHeader().repaint();
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setText(value.toString());
                label.setIcon(column == hoveredColumn ? UIManager.getIcon("FileChooser.newFolderIcon") : null);
                label.setHorizontalTextPosition(SwingConstants.LEFT);
            }
            return c;
        }
    }

    private class HeaderMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JTableHeader header = (JTableHeader) e.getSource();
            int col = header.columnAtPoint(e.getPoint());
            if (col != -1 && e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                tableModel.addColumn("New Column");
            }
        }
    }
}
