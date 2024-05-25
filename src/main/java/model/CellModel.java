package model;

import utils.Evaluator;

public class CellModel {
    String formula;
    String text;
    CellModel() {
        this.formula="";
        this.text="";
    }
    CellModel(String text, TableModel table) {
        this.text = text;
        if(text.startsWith("=")) {
            this.formula = text.substring(1);
            Object result;
            try {
                result = Evaluator.evaluate(this.formula, table);
                this.text = result.toString();

            } catch(RuntimeException e) {
                this.text = "ERROR";
            }
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text, TableModel table) {
        this.text = text;
        if(text.startsWith("=")) {
            this.formula = text.substring(1);
            Object result;
            try {
                result = Evaluator.evaluate(this.formula, table);
            } catch(RuntimeException e) {
                this.text = "ERROR";
            }
        }
    }



}
