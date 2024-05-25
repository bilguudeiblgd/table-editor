package model;

import utils.Evaluator;
import utils.Lexer;
import utils.Token;
import utils.TokenType;

import java.util.*;

public class CellModel {
    String expression;
    String text;
//    Stores what depends on this cell.
    Set<CellModel> dependsOnMe;
    CellModel() {
        this.expression ="";
        this.text="";
    }
    CellModel(String text, TableModel table) {
        this.text = text;
        this.expression = "";
        dependsOnMe = new HashSet<>();
        if(text.startsWith("=")) {
            this.expression = text.substring(1);
            evaluateExpression(table);
            updateDependency(table);
            propogateChanges(table);
        }
        else
            propogateChanges(table);
    }

    public String getText() {
        return text;
    }

    public void setText(String text, TableModel table) {
        this.text = text;
        System.out.println("Writing to cell");
        if(text.startsWith("=")) {
            this.expression = text.substring(1);
            evaluateExpression(table);
            updateDependency(table);
            propogateChanges(table);
        }
        else {
            propogateChanges(table);
        }
    }

    public void evaluateExpression(TableModel table) {
        Object result;
        try {
            result = Evaluator.evaluate(this.expression, table);
            this.text = result.toString();
//            As this cell's value changed we need to propogate the changes to other cells that depend on this cell
        } catch(RuntimeException e) {
            this.text = "ERROR";
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
    public void addDependency(CellModel cell) {
        dependsOnMe.add(cell);
    }
    public void propogateChanges(TableModel table) {

        for ( CellModel model : dependsOnMe) {
            System.out.println("Propogating " + model.getText());
            model.evaluateExpression(table);
        }
    }

//    public String getCellLabel () {
//
//    }



}
