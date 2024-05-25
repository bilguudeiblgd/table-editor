package utils;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentToken = tokens.get(pos);
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        }
    }

    private Token peek(int k) {
        if (pos + k>= tokens.size())
            return new Token(TokenType.EOF, "END");
        return tokens.get(pos + k);
    }

    private Token consume(TokenType type) {
        if (currentToken.getType() == type) {
            Token token = currentToken;
            advance();
            return token;
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken + ", expected: " + type);
        }
    }
    // #TODO: finish Cell parser implementation
    private List<float> L1() {
        Token token = currentToken;

        if(token.getType() == TokenType.VARIABLE) {
            consume(TokenType.VARIABLE);
            Token nextToken = peek(1);
            if (nextToken.getType() == TokenType.BINARY_OPERATOR
            && nextToken.getValue() == ":" ) {
//                Range values
            }
//            return
//          Parse cells and return array
        }

        if (token.getType() == TokenType.NUMBER) {
            consume(TokenType.NUMBER);
            return Integer.parseInt(token.getValue());
        } else if (token.getType() == TokenType.LEFT_PAR) {
            consume(TokenType.LEFT_PAR);
            int result = expr();
            consume(TokenType.RIGHT_PAR);
            return result;
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }

    private int L2() {
//    #TODO:  Function call implementation missing
        int result = L1();
        return result;
    }

    private int L3() {
//      Unary operators work right to left
        int signChange = 1;
        while (currentToken.getType() == TokenType.UNARY_OPERATOR) {
            if (currentToken.getValue().equals("+")) {
                consume(TokenType.UNARY_OPERATOR);
                signChange = 1;
            } else if (currentToken.getValue().equals("-")) {
                consume(TokenType.UNARY_OPERATOR);
                signChange = -1;
            }
        }

        int result = L2();
        result = result * signChange;
        return result;
    }

    private int L4() {
        int result = L3();

        while (currentToken.getValue().equals("*") || currentToken.getValue().equals("/")) {
            if (currentToken.getValue().equals("*")) {
                consume(TokenType.BINARY_OPERATOR);
                result = result * L4();
            } else if (currentToken.getValue().equals("/")) {
                consume(TokenType.BINARY_OPERATOR);
                result = result / L4();
            }
        }
        return result;
    }

    private int L5() {
        int result = L4();

        while (currentToken.getValue().equals("+") || currentToken.getValue().equals("-")) {
            if (currentToken.getValue().equals("+")) {
                consume(TokenType.BINARY_OPERATOR);
                result = result + L4();
            } else if (currentToken.getValue().equals("-")) {
                consume(TokenType.BINARY_OPERATOR);
                result = result - L4();
            }
        }

        return result;
    }
    public int expr() {
        return L5();
    }

    public int parse() {
        return expr();
    }


}
