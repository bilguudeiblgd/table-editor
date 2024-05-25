package model;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
    private List<List<CellModel>> data;
    private List<String> columnNames;

    public TableModel() {
        data = new ArrayList<>();
        columnNames = new ArrayList<>();

        columnNames.add(" ");
        columnNames.add("A");
        columnNames.add("B");
        columnNames.add("C");
        columnNames.add("D");
        columnNames.add("E");

        for(int i = 0; i < 10; i++) {
            ArrayList<CellModel> row = new ArrayList<>();
            for(int j = 0; j < getColumnCount(); j++) {
                row.add(new CellModel(""));
            }
//          Helper labels for the row
            String numberLabel = Integer.toString(i);
            row.set(0, new CellModel(numberLabel));
            data.add(row);
        }

        fireTableRowsInserted(0, data.size() - 1);

    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<CellModel> row = data.get(rowIndex);
        if(row.isEmpty())
            return "";

        return row.get(columnIndex).getText();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data.get(rowIndex).set(columnIndex, new CellModel ((String) aValue) );
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public void addRow(String value) {
        List<CellModel> row = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            row.add(new CellModel(value) );
        }
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void addColumn(String columnName) {
        columnNames.add(columnName);
        for (List<CellModel> row : data) {
            row.add(new CellModel((String) ""));
        }
        fireTableStructureChanged();
    }

    public void removeLastColumn() {
        int lastIndex = columnNames.size() - 1;
        if (lastIndex >= 0) {
            columnNames.remove(lastIndex);
            for (List<CellModel> row : data) {
                row.remove(lastIndex);
            }
            fireTableStructureChanged();
        }
    }
//    We assume query comes in format [A-Z]+[0-9]+$. Example: A1, B2, etc. This was validated by the Lexer.
    public String queryCell(String query) {
        StringBuilder letterPart = new StringBuilder();

        int splitIndex = 0;
        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if(Character.isDigit(ch)) {
                splitIndex = i;
                break;
            }
        }

        String columnLabel = query.substring(0, splitIndex);
        String rowNumbers = query.substring(splitIndex);

        // Convert column label to number
        int colIndex = 0;
        for (int i = 0; i < columnLabel.length(); i++) {
            colIndex = colIndex * 26 + (columnLabel.charAt(i) - 'A' + 1);
        }

        int rowIndex = Integer.parseInt(rowNumbers);

        return data.get(rowIndex).get(colIndex).getText();
    }

}
