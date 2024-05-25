package utils;

import model.TableModel;

import java.util.List;

public class Evaluator {
    public static Object evaluate(String expression, TableModel table) {
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        Parser parser = new Parser(tokens, table);
        return parser.parse();
    }

    public static void main(String[] args) {
        String expression = "-3 + 5 * (10 - 6) / 2";
        Object result = evaluate(expression, new TableModel());
        System.out.println("Result: " + result);
    }
}

