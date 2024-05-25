package utils;

import java.util.List;

public class Evaluator {
    public static int evaluate(String expression) {
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    public static void main(String[] args) {
        String expression = "-3 + 5 * (10 - 6) / 2";
        int result = evaluate(expression);
        System.out.println("Result: " + result);
    }
}

