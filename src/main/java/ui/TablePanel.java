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

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Enable grid lines
        table.setShowGrid(true);

        // Set the grid color (optional)
        table.setGridColor(java.awt.Color.BLACK);

        rowInputField = new JTextField(20);
        columnInputField = new JTextField(20);
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        table.addMouseListener(new MouseListener());
//        add(controlPanel, BorderLayout.SOUTH);

    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                System.out.println("Right mouse clicked");
                JTable table = (JTable) e.getSource();

                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (col != -1 && row != -1) {
                    JPopupMenu popupMenu = createHeaderPopupMenu(col, row);
                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        }
    }

    private JPopupMenu createHeaderPopupMenu(int colIndex, int rowIndex) {
        JPopupMenu popupMenu = new JPopupMenu();
        System.out.println("Row index: " + rowIndex);
        System.out.println("Column index: " + colIndex);
        JMenuItem addColumnLeft = new JMenuItem("Add Column Left");
        addColumnLeft.addActionListener(e -> {
            tableModel.insertColumn(colIndex, tableModel.createEmptyColumn());
        });
        popupMenu.add(addColumnLeft);

        JMenuItem addColumnRight = new JMenuItem("Add Column Right");
        addColumnRight.addActionListener(e -> {
            tableModel.insertColumn(colIndex + 1, tableModel.createEmptyColumn());
        });
        popupMenu.add(addColumnRight);

        JMenuItem addRowUp = new JMenuItem("Add Row Up");
        addRowUp.addActionListener(e -> {
            tableModel.addRow("");
        });
        popupMenu.add(addRowUp);

        JMenuItem addRowDown = new JMenuItem("Add Row Down");
        addRowDown.addActionListener(e -> {
            tableModel.addRow("");
        });
        popupMenu.add(addRowDown);

        JMenuItem deleteCol = new JMenuItem("Delete Column");
        deleteCol.addActionListener(e -> {
            tableModel.deleteColumn(colIndex);
        });
        popupMenu.add(deleteCol);


        return popupMenu;
    }

//    private class HeaderRenderer extends DefaultTableCellRenderer {
//        private final JTable table;
//        private int hoveredColumn = -1;
//
//        public HeaderRenderer(JTable table) {
//            this.table = table;
//            table.getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseMoved(MouseEvent e) {
//                    int col = table.columnAtPoint(e.getPoint());
//                    if (col != hoveredColumn) {
//                        hoveredColumn = col;
//                        table.getTableHeader().repaint();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            if (c instanceof JLabel) {
//                JLabel label = (JLabel) c;
//                label.setText(value.toString());
//                label.setIcon(column == hoveredColumn ? UIManager.getIcon("FileChooser.newFolderIcon") : null);
//                label.setHorizontalTextPosition(SwingConstants.LEFT);
//            }
//            return c;
//        }
//    }
//
//    private class HeaderMouseListener extends MouseAdapter {
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            JTableHeader header = (JTableHeader) e.getSource();
//            int col = header.columnAtPoint(e.getPoint());
//            if (col != -1 && e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
//                tableModel.addColumn("New Column");
//            }
//        }
//    }
}
