package model;

import utils.Evaluator;

public class CellModel {
    String formula;
    String text;
    CellModel() {
        this.formula="";
        this.text="";
    }
    CellModel(String text) {
        this.text = text;
        if(text.startsWith("=")) {
            this.formula = text.substring(1);
            this.text = Integer.toString(Evaluator.evaluate(this.formula));
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if(text.startsWith("=")) {
            this.formula = text.substring(1);
            this.text = Integer.toString(Evaluator.evaluate(this.formula));
        }
    }



}
