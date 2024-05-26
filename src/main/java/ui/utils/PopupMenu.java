package ui.utils;

import javax.swing.*;
import model.TableModel;

public class PopupMenu {
    public static JPopupMenu createHeaderPopupMenu(int colIndex, int rowIndex, TableModel tableModel) {
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
            tableModel.insertRow(rowIndex, tableModel.createEmptyRow());
        });
        popupMenu.add(addRowUp);

        JMenuItem addRowDown = new JMenuItem("Add Row Down");
        addRowDown.addActionListener(e -> {
            tableModel.insertRow(rowIndex + 1, tableModel.createEmptyRow());
        });
        popupMenu.add(addRowDown);

        JMenuItem deleteCol = new JMenuItem("Delete Column");
        deleteCol.addActionListener(e -> {
            tableModel.deleteColumn(colIndex);
        });
        popupMenu.add(deleteCol);

        JMenuItem deleteRow = new JMenuItem("Delete Row");
        deleteRow.addActionListener(e -> {
            tableModel.deleteRow(rowIndex);
        });
        popupMenu.add(deleteRow);

        return popupMenu;
    }
}
