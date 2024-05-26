package model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                row.add(new CellModel("",this));
            }
//          Helper labels for the row
            String numberLabel = Integer.toString(i);
            row.set(0, new CellModel(numberLabel,this));
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
            throw new RuntimeException("Row is empty");
        return row.get(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
//      Helper row indexes
        if (columnIndex == 0) return false;
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        CellModel currentModel = data.get(rowIndex).get(columnIndex);
        currentModel.setValue((String) aValue ,this);
        data.get(rowIndex).set(columnIndex, currentModel );
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public String getNextColumnLabel() {
        // Convert the last column label to uppercase
        String lastCol = columnNames.getLast().toUpperCase();

        // Start with an empty StringBuilder to build the next column label
        StringBuilder nextColLabel = new StringBuilder();

        // Iterate through each character in the last column label
        for (int i = lastCol.length() - 1; i >= 0; i--) {
            char currentChar = lastCol.charAt(i);

            // If the current character is 'Z', carry over to the next position
            if (currentChar == 'Z') {
                // Replace 'Z' with 'A' and add 'A' to the next position
                nextColLabel.insert(0, 'A');
            } else {
                // Increment the current character by 1
                nextColLabel.insert(0, (char) (currentChar + 1));

                // Append the remaining characters from the last column label
                nextColLabel.insert(0, lastCol.substring(0, i));

                // No further carry-over needed, break the loop
                break;
            }
        }

        return nextColLabel.toString();
    }


    public void addRow(String value) {
        List<CellModel> row = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            row.add(new CellModel(value, this) );
        }
        data.add(row);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeRow(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    public List<CellModel> createEmptyColumn() {
        List<CellModel> column = new ArrayList<>();
        for(int i = 0; i < getRowCount(); i++) {
            column.add(new CellModel("",this));
        }
        return column;
    }


    public void insertColumn(int columnIndex, List<CellModel> columnData) {
//        First create empty last.
        columnNames.add(getNextColumnLabel());
        for (List<CellModel> row : data) {
            row.add(new CellModel((String) "", this));
        }
        Set<CellModel> dependedModels = new HashSet<>();
//        For each column move 1 to right
        for (int i = getColumnCount() - 1; i > columnIndex; i--) {
//            For each element in the column
            for(int j = 0; j < getRowCount(); j++) {
                dependedModels.addAll(data.get(j).get(i-1).getDependsOnMe());
                CellModel newCell = new CellModel((String) data.get(j).get(i-1).getValue(), this);
                data.get(j).set(i, newCell);
                data.get(j).set(i-1, new CellModel("", this));
            }
        }

        for (int i = 0; i < getRowCount(); i++) {
            data.get(i).set(columnIndex, columnData.get(i));
        }

        for(CellModel model : dependedModels) {
            model.revaluate(this);
        }

        fireTableStructureChanged();
    }

    public void deleteColumn(int columnIndex) {
        // Remove the column from columnNames
        Set<CellModel> dependedModels = new HashSet<>();
//        For each column move 1 to right
        for (int i = columnIndex ; i < getColumnCount() - 1; i++) {
//            For each element in the column
            for(int j = 0; j < getRowCount(); j++) {
                dependedModels.addAll(data.get(j).get(i+1).getDependsOnMe());
                CellModel newCell = new CellModel((String) data.get(j).get(i+1).getValue(), this);
                data.get(j).set(i, newCell);
                data.get(j).set(i+1, new CellModel("", this));
            }
        }

        for(CellModel model : dependedModels) {
            model.revaluate(this);
        }

        // Notify listeners that the table structure has changed
        fireTableStructureChanged();
    }


    public Boolean isValidCell(int rowIndex, int colIndex) {
        if(data.isEmpty()) return false;
        if(data.getFirst().isEmpty()) return false;

        if(rowIndex >= data.size()) {
            return false;
        }
        return colIndex < data.getFirst().size();
    }
//    We assume query comes in format [A-Z]+[0-9]+$. Example: A1, B2, etc. This was validated by the Lexer.

    public int[] getRowColIndex(String query) {
        StringBuilder letterPart = new StringBuilder();
        System.out.println(query);
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
        System.out.println(rowIndex + " " + colIndex);
        return new int[]{rowIndex, colIndex};
    }

    public CellModel queryCellModel(String query) {
        int[] rowColIndex = getRowColIndex(query);
        int rowIndex = rowColIndex[0];
        int colIndex = rowColIndex[1];

        if(!isValidCell(rowIndex, colIndex)) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        return data.get(rowIndex).get(colIndex);
    }

    public Object queryCellValue(String query) {
        String value = queryCellModel(query).getDisplayText();

        Object valueObject = new Object();
        try {
            Float floatValue = Float.parseFloat(value);
            System.out.println("Parsed float: " + floatValue);
            valueObject = floatValue;
        } catch(NumberFormatException e) {
            System.err.println("Invalid number format: " + value);
        }

        return valueObject;
    }
    public List<CellModel> queryCellRangeModels(String query) {
        int splitIndex = 0;

        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);
            if(ch == ':') {
                splitIndex = i;
                break;
            }
        }
        String startLabel = query.substring(0, splitIndex);
        String endLabel = query.substring(splitIndex+1);

        int[] rowColIndex1 = getRowColIndex(startLabel);
        int[] rowColIndex2 = getRowColIndex(endLabel);


        int topRowIndex = Math.max(rowColIndex1[0], rowColIndex2[0]);
        int rightColIndex = Math.max(rowColIndex1[1], rowColIndex2[1]);

        int bottomRowIndex = Math.min(rowColIndex1[0], rowColIndex2[0]);
        int leftColIndex = Math.min(rowColIndex1[1], rowColIndex2[1]);


        if(!isValidCell(bottomRowIndex, rightColIndex)) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        List<CellModel> results = new ArrayList<>();

        for (int i = bottomRowIndex; i <= topRowIndex; i++) {
            for (int j = leftColIndex; j <= rightColIndex; j++) {
                results.add(data.get(i).get(j));
            }
        }
        return results;
    }

    public List<Object> queryCellRangeValues(String query) {
        List<Object> results = new ArrayList<>();
        List<CellModel> models = queryCellRangeModels(query);
        for(CellModel model : models) {
            String value = model.getDisplayText();
            try {
                Float floatValue = Float.parseFloat(value);
                System.out.println("Parsed float: " + floatValue);
                results.add(floatValue);
            } catch(NumberFormatException e) {
                System.err.println("Invalid number format: " + value);
            }
        }
        return results;

    }


}
