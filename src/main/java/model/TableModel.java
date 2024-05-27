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

//        Initialize with 10 cols
        columnNames.add(" ");
        columnNames.add("A");

        for(int i = 0; i < 8; i++) {
            columnNames.add(getNextColumnLabel());
        }

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
//      Let's not change the label row
        if(columnIndex == 0) return;
        data.get(rowIndex).get(columnIndex).setValue((String) aValue ,this);
//      #TODO: Tracking dependencies for knowing which cells changed will make it efficient
        fireTableDataChanged();
//      After the table changes
    }

    public String getNextColumnLabel() {
        // Convert the last column label to uppercase
        String lastCol = columnNames.getLast().toUpperCase();

        // Convert the column label to an integer
        int colNumber = 0;
        for (int i = 0; i < lastCol.length(); i++) {
            colNumber = colNumber * 26 + (lastCol.charAt(i) - 'A' + 1);
        }

        // Increment the column number by 1
        colNumber++;

        // Convert the integer back to a column label
        StringBuilder nextColLabel = new StringBuilder();
        while (colNumber > 0) {
            colNumber--; // Adjust for 0-based index
            nextColLabel.insert(0, (char) ('A' + colNumber % 26));
            colNumber /= 26;
        }

        return nextColLabel.toString();
    }

    public List<CellModel> createEmptyColumn() {
        List<CellModel> column = new ArrayList<>();
        for(int i = 0; i < getRowCount(); i++) {
            column.add(new CellModel("",this));
        }
        return column;
    }
    public List<CellModel> createEmptyRow() {
        List<CellModel> column = new ArrayList<>();
        for(int i = 0; i < getColumnCount(); i++) {
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

    public void insertRow(int rowIndex, List<CellModel> rowData) {

//      Append one more row
        List<CellModel> emptyRow = createEmptyRow();
//      Get the latest label
        emptyRow.set(0, new CellModel(Integer.toString(data.size()), this));
        data.add(emptyRow);


        Set<CellModel> dependedModels = new HashSet<>();
//        For each row move 1 to down
        for (int i = getRowCount() - 1; i > rowIndex; i--) {
//            For each element in the column
            for(int j = 1; j < getColumnCount(); j++) {
                dependedModels.addAll(data.get(i-1).get(j).getDependsOnMe());
                CellModel newCell = new CellModel((String) data.get(i-1).get(j).getValue(), this);
                data.get(i).set(j, newCell);
                data.get(i-1).set(j, new CellModel("", this));
            }
        }
//      Insert the actual row, j = 0, is reserved for labels.
        for (int j = 1; j < getColumnCount(); j++) {
            data.get(rowIndex).set(j, rowData.get(j));
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
                dependedModels.addAll(data.get(j).get(i).getDependsOnMe());
                CellModel newCell = new CellModel((String) data.get(j).get(i+1).getValue(), this);
                data.get(j).set(i, newCell);
                data.get(j).set(i+1, new CellModel("", this));
            }
        }

        if(getColumnCount() > 2) {

            this.columnNames.removeLast();
            // Remove the last column from each row
            for (List<CellModel> row : data) {
                dependedModels.addAll(row.getLast().getDependsOnMe());
                row.removeLast();
            }
        }

        // Notify listeners that the table structure has changed
        fireTableStructureChanged();
        fireTableDataChanged();

        for(CellModel model : dependedModels) {
            model.revaluate(this);
        }
    }

    public void deleteRow(int rowIndex) {
        // Remove the column from columnNames
        Set<CellModel> dependedModels = new HashSet<>();
//        For each column move 1 to right
        for (int i = rowIndex ; i < getRowCount() - 1; i++) {
//            For each element in the column
//            First element of the column is the labels
            for(int j = 1; j < getColumnCount(); j++) {
                dependedModels.addAll(data.get(i).get(j).getDependsOnMe());
//                Get myself out of those dependedModels
                CellModel newCell = new CellModel((String) data.get(i+1).get(j).getValue(), this);
                data.get(i).set(j, newCell);
                data.get(i+1).set(j, new CellModel("", this));
            }
        }

        if (getRowCount() > 2) {
            for (CellModel model : data.getLast()) {
                dependedModels.addAll(model.getDependsOnMe());
            }
            data.removeLast();
        }


        for(CellModel model : dependedModels) {
            model.revaluate(this);
        }

        // Notify listeners that the table structure has changed
        fireTableStructureChanged();
        fireTableDataChanged();
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
        return queryCellModel(query).getDisplayText();
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
            results.add(value);
        }
        return results;

    }


}
