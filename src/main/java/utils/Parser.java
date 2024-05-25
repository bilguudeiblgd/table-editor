package utils;

import model.TableModel;
import utils.functions.SumFunction;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private Token currentToken;
    private TableModel table;

    public Parser(List<Token> tokens, TableModel table) {
        this.tokens = tokens;
        this.currentToken = tokens.get(pos);
        this.table = table;
    }

    private void advance() {
        pos++;
        if (pos < tokens.size()) {
            currentToken = tokens.get(pos);
        }
    }

    private Token peek(int k) {
        if (pos + k >= tokens.size())
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

    // TODO: finish Cell parser implementation
    private List<Object> L1() {
        List<Object> result = new ArrayList<>();
        Token token = currentToken;

        if (token.getType() == TokenType.CELL) {
            consume(TokenType.CELL);
            Token nextToken = peek(1);
            Object obj = table.queryCell(token.getValue());

            System.out.println("table query A1: " + obj.toString());
            result.add(obj);
            return result;
        }

        if(token.getType() == TokenType.CELL_RANGE) {
            consume(TokenType.CELL_RANGE);

        }

        if (token.getType() == TokenType.NUMBER) {
            consume(TokenType.NUMBER);
            result.add(Integer.parseInt(token.getValue()));
            return result;
        } else if (token.getType() == TokenType.LEFT_PAR) {
            consume(TokenType.LEFT_PAR);
            Object exprResult = expr();
            consume(TokenType.RIGHT_PAR);
            result.add(exprResult);
            return result;
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }

    private List<Object> L2() {
//        If not function will go to L1.
        if (currentToken.getType() == TokenType.FUNCTION) {
            switch (currentToken.getValue()) {
                case "sum":
                    System.out.println("Parsing sum function");
                    SumFunction sumFunction = new SumFunction();
                    consume(TokenType.FUNCTION);
                    List<Object> arguments = new ArrayList<>();
                    consume(TokenType.LEFT_PAR);

                    while(currentToken.getType() != TokenType.RIGHT_PAR) {
//                        Can be just expr() or A1:B1 which is L1.
                        List<Object> argumentPack = expr();
                        System.out.println("Parsing argument: " + argumentPack.getFirst());
                        arguments.addAll(argumentPack);

                        if(currentToken.getType() == TokenType.COMMA) {
                            consume(TokenType.COMMA);
                        }
                    }

                    consume(TokenType.RIGHT_PAR);
                    sumFunction.addArguments(arguments);

                    List<Object> funcResults = new ArrayList<>();

                    funcResults.add(sumFunction.calculate());
//                  Functions have 1 return for now.
                    return funcResults;
            }
        }

        return L1();
    }

    private List<Object> L3() {
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

        List<Object> results = L2();
        Object result = results.getFirst();

        System.out.println(result);
        if (result instanceof Number) {
            float modifiedValue = ((Number) result).floatValue() * signChange;
            results.set(0, modifiedValue);
            return results;
        } else {
            throw new RuntimeException("Invalid type for unary operation: " + result);
        }
    }

    private List<Object> L4() {
        List<Object> results = L3();
        Object result = results.get(0);

        while (currentToken.getValue().equals("*") || currentToken.getValue().equals("/")) {
            if (currentToken.getValue().equals("*")) {
                consume(TokenType.BINARY_OPERATOR);
                Object right = L3();
                if (result instanceof Number && right instanceof Number) {
                    result = ((Number) result).floatValue() * ((Number) right).floatValue();
                    results.set(0, result);
                } else {
                    throw new RuntimeException("Invalid type for multiplication: " + result + " * " + right);
                }
            } else if (currentToken.getValue().equals("/")) {
                consume(TokenType.BINARY_OPERATOR);
                Object right = L3();
                if (result instanceof Number && right instanceof Number) {
                    result = ((Number) result).floatValue() / ((Number) right).floatValue();
                    results.set(0, result);
                } else {
                    throw new RuntimeException("Invalid type for division: " + result + " / " + right);
                }
            }
        }
        return results;
    }

    private List<Object> L5() {
        List<Object> results = L4();

        if(results.isEmpty())
            throw new RuntimeException("No return value: " + results);
        Object result = results.getFirst();

        while (currentToken.getValue().equals("+") || currentToken.getValue().equals("-")) {
            if (currentToken.getValue().equals("+")) {
                consume(TokenType.BINARY_OPERATOR);
                Object right = L4();
                if (result instanceof Number && right instanceof Number) {
                    result = ((Number) result).floatValue() + ((Number) right).floatValue();
                    results.set(0, result);
                } else {
                    throw new RuntimeException("Invalid type for addition: " + result + " + " + right);
                }
            } else if (currentToken.getValue().equals("-")) {
                consume(TokenType.BINARY_OPERATOR);
                Object right = L4();
                if (result instanceof Number && right instanceof Number) {
                    result = ((Number) result).floatValue() - ((Number) right).floatValue();
                    results.set(0, result);
                } else {
                    throw new RuntimeException("Invalid type for subtraction: " + result + " - " + right);
                }
            }
        }
        return results;
    }

    public List<Object> expr() {
        return L5();
    }

    public Object parse() {
        List<Object> results = expr();
//      In the top-most node we will have 1 value.
//      We use results as an array as we have some A1:B1 range values which will look like (A1, B1) and so on.
//      Thus to have them flow through parser we're using general list of return values.
        if(results.size() != 1)
            throw new RuntimeException("Returned more than one value: ");
        return results.getFirst();
    }
}
