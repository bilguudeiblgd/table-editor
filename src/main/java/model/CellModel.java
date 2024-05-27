package model;
import model.utils.CycleDetector;

import parser.Evaluator;
import parser.Lexer;
import parser.Token;
import parser.TokenType;
import java.util.*;

public class CellModel {
//    Value is any input. Either expression or just text.
    String value;
//    What flows into the parser
    String expression;
//    What is displayed on the screen
    String displayText;
//    Stores which cells depends on this cell.
    Set<CellModel> dependsOnMe;
    CellModel() {
        this.value = "";
        this.expression ="";
        this.displayText ="";
    }
    CellModel(String value, TableModel table) {
        this.value = value;
        this.expression = "";
        dependsOnMe = new HashSet<>();
        setValue(value, table);
    }

    public String getDisplayText() {
        return this.displayText;
    }
    public String getValue() {
        return this.value;
    }
    private String getExpression() {
        return this.expression;
    }

    public void setValue(String value, TableModel table) {
        this.value = value;
        if(this.value.startsWith("=")) {
            this.expression = this.value.substring(1);
            try {
                evaluateExpression(table);
                updateDependency(table);
                propogateChanges(table);
            } catch (Exception e) {
                e.printStackTrace();
                this.displayText = "ERR#";
            }
        }
        else {
            this.displayText = this.value;
            try {
                propogateChanges(table);
            } catch (Exception e) {
                e.printStackTrace();
                this.displayText = "ERR#";
            }
        }
    }

    public void evaluateExpression(TableModel table) {
        Object result;
        try {
            result = Evaluator.evaluate(this.expression, table);
            if(result.toString().isEmpty())
                this.displayText = "ERR#";
            else
                this.displayText = result.toString();
//            As this cell's value changed we need to propogate the changes to other cells that depend on this cell
        } catch(RuntimeException e) {
            this.displayText = "ERR#";
        }
    }
    public void updateDependency(TableModel table) {
        Lexer lexer = new Lexer(this.expression);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            if (token.getType() == TokenType.CELL) {
                CellModel model = table.queryCellModel(token.getValue());
                if(model == this)
                    throw new RuntimeException("Self dependency loop!");
                model.addDependency(this);
            }
            if (token.getType() == TokenType.CELL_RANGE) {
                for(CellModel model : table.queryCellRangeModels(token.getValue())) {
                    if(model == this)
                        throw new RuntimeException("Self dependency loop!");
                    model.addDependency(this);
                }
            }
        }
    }
    public Set<CellModel> getDependsOnMe() {
        return this.dependsOnMe;
    }
    public void addDependency(CellModel cell) {
        dependsOnMe.add(cell);
    }
    public void propogateChanges(TableModel table) {
        if(CycleDetector.hasCycle(this)) {
            throw new RuntimeException("Dependency cycle detected!");
        }
        for ( CellModel model : dependsOnMe) {
            System.out.println("Propogating to" + model);
            model.revaluate(table);
        }
    }
    public void revaluate(TableModel table) {
        if(this.value.startsWith("=")) {
            try {
                evaluateExpression(table);
                updateDependency(table);
                propogateChanges(table);
            } catch (Exception e) {
                e.printStackTrace();
                this.displayText = "ERR#";
            }
        }
        else {
            propogateChanges(table);
        }
    }


//    public String getCellLabel () {
//
//    }



}
