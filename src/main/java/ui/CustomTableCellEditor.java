package ui;

import model.CellModel;

import javax.swing.*;
import java.awt.*;

public class CustomTableCellEditor extends DefaultCellEditor {
    private JTextField textField;

    public CustomTableCellEditor() {
        super(new JTextField());
        textField = (JTextField) getComponent();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Set the editor value to the actual underlying value
        CellModel cell = (CellModel) value;
        textField.setText(cell.getValue());
        return textField;
    }

}
