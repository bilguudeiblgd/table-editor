package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import model.TableModel;
import ui.utils.PopupMenu;

public class TablePanel extends JPanel {
    private JTable table;
    private TableModel tableModel;
    private JTextField rowInputField;
    private JTextField columnInputField;
    private JButton addTenRowsButton; // New button for adding 10 more rows
    private JButton addTenColumnsButton; // New button for adding 10 more columns

    public TablePanel() {
        setLayout(new BorderLayout());

        tableModel = new TableModel();
        table = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new CustomTableCellRenderer();
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                return new CustomTableCellEditor();
            }
        };


        // Wrap the table in a scroll pane to enable scrolling
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Enable grid lines
        table.setShowGrid(true);

        // Resize the cells in the labelColumn
        TableColumn labelColumn = table.getColumnModel().getColumn(0);
        labelColumn.setMaxWidth(15);

        // Set the grid color (optional)
        table.setGridColor(Color.DARK_GRAY);

        rowInputField = new JTextField(20);
        columnInputField = new JTextField(20);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        table.addMouseListener(new MouseListener());

        // Create the "Add 10 More Rows" button
        addTenRowsButton = new JButton("Add 10 More Rows");
        addTenRowsButton.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                tableModel.insertRow(table.getRowCount(), tableModel.createEmptyRow());
            }
        });

        // Create the "Add 10 More Columns" button
        addTenColumnsButton = new JButton("Add 10 More Columns");
        addTenColumnsButton.addActionListener(e -> {
            int colCount = tableModel.getColumnCount();
            for (int i = 0; i < 10; i++) {
                tableModel.insertColumn(table.getColumnCount(), tableModel.createEmptyColumn());
            }
        });

        // Create a panel to hold the "Add 10 More Rows" button and add it to the bottom of the table
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addTenRowsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Create a panel to hold the "Add 10 More Columns" button and add it to the right side of the table
        JPanel rightButtonPanel = new JPanel();
        rightButtonPanel.setLayout(new BorderLayout());
        rightButtonPanel.add(addTenColumnsButton, BorderLayout.SOUTH);
        add(rightButtonPanel, BorderLayout.EAST);
    }

    private void printColumnDetails() {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            System.out.println("Column Index: " + i);
            System.out.println("Preferred Width: " + column.getPreferredWidth());
            System.out.println("Min Width: " + column.getMinWidth());
            System.out.println("Max Width: " + column.getMaxWidth());
            System.out.println("Width: " + column.getWidth());
            System.out.println("Header Value: " + column.getHeaderValue());
            System.out.println("------------------------------------");
        }
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                System.out.println("Right mouse clicked");
                JTable table = (JTable) e.getSource();

                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                // Col 0 is our number labels
                if (col != 0 && col != -1 && row != -1) {
                    JPopupMenu popupMenu = PopupMenu.createHeaderPopupMenu(col, row, tableModel);
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        }
    }


}
