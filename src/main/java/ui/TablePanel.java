package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.IOException;

import model.CellModel;
import model.TableModel;
import ui.utils.PopupMenu;

public class TablePanel extends JPanel {
    private JTable table;
    private TableModel tableModel;
    private JTextField rowInputField;
    private JTextField columnInputField;
    private JButton addTenRowsButton; // New button for adding 10 more rows
    private JButton addTenColumnsButton; // New button for adding 10 more columns
    private Point selectionStart;
    private Point selectionEnd;

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

        // Disable row selection
        table.setRowSelectionAllowed(false);

        // Enable cell selection
        table.setCellSelectionEnabled(true);

        // Enable grid lines
        table.setShowGrid(true);

        // Resize the cells in the labelColumn
        TableColumn labelColumn = table.getColumnModel().getColumn(0);
        labelColumn.setPreferredWidth(20);

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
        addTenColumnsButton = new JButton("+ Columns");
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


        // Create a copy action and bind it to Ctrl+C
        Action copyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Copy action: " + e.toString());
                copySelectedCellsToClipboard();
            }
        };
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "copyAction");

        // Create a paste action and bind it to Ctrl+V
        Action pasteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteFromClipboard();
            }
        };
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "pasteAction");

        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedCells();
            }
        };
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "deleteAction");
        table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteAction");

        table.getActionMap().put("deleteAction", deleteAction);
        table.getActionMap().put("pasteAction", pasteAction);
        table.getActionMap().put("copyAction", copyAction);

    }


    private void copySelectedCellsToClipboard() {
        StringBuilder clipboardData = new StringBuilder();
        int[] rows = table.getSelectedRows();
        int[] cols = table.getSelectedColumns();

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                CellModel cell = (CellModel) table.getValueAt(rows[i], cols[j]);
//              Clipboard: rows:cols:expression
                clipboardData.append( i + selectionStart.y).append("-:-").append(j + selectionStart.x).append("-:-").append(cell.getValue());
                if (j < cols.length - 1) {
                    clipboardData.append('\t');
                }
            }
            clipboardData.append('\n');
        }
        StringSelection stringSelection = new StringSelection(clipboardData.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }


    private void pasteFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                String[] rows = clipboardData.split("\n");
                int startRow = table.getSelectedRow();
                int startCol = table.getSelectedColumn();
//              Parsing the clipboard and putting it back.
                for (int i = 0; i < rows.length; i++) {
                    String[] cells = rows[i].split("\t");
                    for (int j = 0; j < cells.length; j++) {
                        String[] parts = cells[j].split("-:-");
//                      #TODO:  Use row,col for translating the expression's coordinates
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        String value = "";
//                      If it's not empty cell
                        if(parts.length == 3) {
                            value = parts[2];
                        }
                        if(startRow + i >= table.getRowCount())
                            tableModel.insertRow(startRow + i, tableModel.createEmptyRow());
                        if(startCol + j >= table.getColumnCount())
                            tableModel.insertColumn(startCol + j, tableModel.createEmptyColumn());
                        table.setValueAt(value, startRow + i, startCol + j);
                    }
                }
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void selectCells(Point start, Point end) {
        if (start != null && end != null) {
            int minX = Math.min(start.x, end.x);
            int minY = Math.min(start.y, end.y);
            int maxX = Math.max(start.x, end.x);
            int maxY = Math.max(start.y, end.y);
            table.getSelectionModel().addSelectionInterval(minY, maxY);
            table.getColumnModel().getSelectionModel().addSelectionInterval(minX, maxX);
        }
    }

    private void deleteSelectedCells() {
        int[] rows = table.getSelectedRows();
        int[] cols = table.getSelectedColumns();

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
//                CellModel cell = (CellModel) table.getValueAt(rows[i], cols[j]);
//                cell.setValue("", tableModel);
                table.setValueAt("", rows[i], cols[j]);
            }
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

        @Override
        public void mousePressed(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            selectionStart = new Point(col, row);
            selectionEnd = new Point(col, row);
            table.getSelectionModel().clearSelection();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            selectionEnd = new Point(col, row);
            selectCells(selectionStart, selectionEnd);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            selectionEnd = new Point(col, row);
            selectCells(selectionStart, selectionEnd);
        }


    }


}
