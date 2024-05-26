package ui;

import model.CellModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Customize the displayed value
//            String displayValue = getDisplayValue(value);
        CellModel cell = (CellModel) value;
//        if (isSelected)
//            System.out.println("Selected -> Row: "+ row + " Column: "+ column);
//        if (hasFocus)
//            System.out.println("hasFocus -> Row: "+ row + " Column: "+ column);
        return super.getTableCellRendererComponent(table, cell.getDisplayText(), isSelected, hasFocus, row, column);
    }

//        private String getDisplayValue(Object value) {
//            // Convert the underlying value to the displayed value
//            if (value instanceof Integer) {
//                return "Value: " + value;
//            }
//            return value.toString();
//        }
}