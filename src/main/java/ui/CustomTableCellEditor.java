package ui;

import model.CellModel;

import javax.swing.*;
import java.awt.*;

public class CustomTableCellEditor extends DefaultCellEditor {
    private JTextField textField;

    public CustomTableCellEditor() {
        super(new JTextField());
        textField = (JTextField) getComponent();
        textField.setBorder(BorderFactory.createEmptyBorder());

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Set the editor value to the actual underlying value
        CellModel cell = (CellModel) value;
        textField.setText(cell.getValue());

        if (isSelected) {
            textField.setBackground(Color.LIGHT_GRAY);
            textField.setForeground(table.getSelectionForeground());
        } else {
            textField.setBackground(table.getBackground());
            textField.setForeground(table.getForeground());
        }

        return textField;
    }

}
