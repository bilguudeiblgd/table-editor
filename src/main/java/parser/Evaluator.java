package parser;

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
}

