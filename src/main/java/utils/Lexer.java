package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexer {
    private final String input;
    private int position;
    final private int length;
    final private HashMap<String, Boolean> functionNames = new HashMap<>();

    public Lexer(String input) {
        this.input = input;
        this.length = input.length();
        this.functionNames.put("sum", true);
        this.position = 0;
    }

    private char peek() {
        if (position >= length) return '\0';
        return input.charAt(position);
    }

    private char advance() {
        return input.charAt(position++);
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(peek())) {
            advance();
        }
    }
//
    public static boolean isValidCellVariable(String input) {
        String regex = "^[A-Z]+[0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }


    private Token lexNumber() {
        StringBuilder number = new StringBuilder();
        while (Character.isDigit(peek()) || peek() == '.') {
            number.append(advance());
        }
        return new Token(TokenType.NUMBER, number.toString());
    }

    private Token lexIdentifier() {
        StringBuilder identifier = new StringBuilder();
        while (Character.isLetter(peek()) || Character.isDigit(peek()) || peek() == '_') {
            identifier.append(advance());
        }
        String value = identifier.toString();
        if (this.functionNames.containsKey(value))
            return new Token(TokenType.FUNCTION, value);

//      Variable are CELL variables.
        if (!isValidCellVariable(value))
            throw new RuntimeException("Wrong cell value");
        return new Token(TokenType.VARIABLE, value);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < length) {
            char current = peek();

            if (Character.isWhitespace(current)) {
                skipWhitespace();
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(lexNumber());
                continue;
            }

            if (Character.isLetter(current)) {
                tokens.add(lexIdentifier());
                continue;
            }

            switch (current) {
                case '(':
                    tokens.add(new Token(TokenType.LEFT_PAR, Character.toString(current)));
                    advance();
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_PAR, Character.toString(current)));
                    advance();
                    break;
                case '+':
                case '-':
                    // Determine if it's a binary or unary operator
                    if (tokens.isEmpty() || tokens.getLast().type == TokenType.LEFT_PAR ||
                            tokens.getLast().type == TokenType.BINARY_OPERATOR ||
                            tokens.getLast().type == TokenType.UNARY_OPERATOR) {
                        tokens.add(new Token(TokenType.UNARY_OPERATOR, Character.toString(current)));
                    } else {
                        tokens.add(new Token(TokenType.BINARY_OPERATOR, Character.toString(current)));
                    }
                    advance();
                    break;
                case '*':
                case '/':
                case '^':
                    tokens.add(new Token(TokenType.BINARY_OPERATOR, Character.toString(current)));
                    advance();
                    break;
                default:
                    throw new RuntimeException("Unexpected character: " + current);
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

//    public static void main(String[] args) {
//        Lexer lexer = new Lexer("sin(x) + cos(y) * z - 42");
//        List<Token> tokens = lexer.tokenize();
//        for (Token token : tokens) {
//            System.out.println(token);
//        }
//    }
}