package parser;

import model.TableModel;
import parser.functions.*;
import parser.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private Token currentToken;
//    Parser needs current table model for parsing cell values
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

    List<Object> parseFunctionArguments() {
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

        return arguments;
    }
    // Level 1: End values. L1 return actual values we can do operations on.
    private List<Object> L1() {
        List<Object> result = new ArrayList<>();
        Token token = currentToken;

        if (token.getType() == TokenType.CELL) {
            consume(TokenType.CELL);
            Token nextToken = peek(1);
            Object obj = table.queryCellValue(token.getValue());

            System.out.println("table query: " + obj.toString());
            result.add(obj);
            return result;
        }

        if(token.getType() == TokenType.CELL_RANGE) {
            consume(TokenType.CELL_RANGE);
            result.addAll(table.queryCellRangeValues(token.getValue()));
            return result;
        }

        if (token.getType() == TokenType.NUMBER) {
            consume(TokenType.NUMBER);
            result.add(Integer.parseInt(token.getValue()));
            return result;

        } else if (token.getType() == TokenType.LEFT_PAR) {
            consume(TokenType.LEFT_PAR);
            List<Object> exprResult = expr();
            if (exprResult.isEmpty()) {
                throw new RuntimeException("Empty expression: " + token);
            }
            consume(TokenType.RIGHT_PAR);
            result.add(exprResult.getFirst());
            return result;
        } else {
            throw new RuntimeException("Unexpected token: " + token);
        }
    }


//  L2 is for taking care of functions.
    private List<Object> L2() {
//        If not function will go to L1.
        if (currentToken.getType() == TokenType.FUNCTION) {
            Function function = switch (currentToken.getValue()) {
                case "SUM" -> new SumFunction();
                case "AVG" -> new AvgFunction();
                case "POW" -> new PowFunction();
                case "MIN" -> new MinFunction();
                case "MAX" -> new MaxFunction();
                case "CONCAT" -> new ConcatFunction();
                case "COUNT" -> new CountFunction();
                default -> throw new UnsupportedOperationException("Unsupported function: " + currentToken.getValue());
            };

            //            Parse array
            consume(TokenType.FUNCTION);

            List<Object> arguments = parseFunctionArguments();
            function.addArguments(arguments);

            List<Object> funcResults = new ArrayList<>();

            funcResults.add(function.calculate());
            return funcResults;

        }

        return L1();
    }
//  L3 for UNARY_OPERATORS. UNARY_OPERATORS operate right->to->left.
    private List<Object> L3() {
        int signChange = 0;

        while (currentToken.getType() == TokenType.UNARY_OPERATOR) {
            if (currentToken.getValue().equals("+")) {
                signChange = 1;
            } else if (currentToken.getValue().equals("-")) {
                signChange = -1;
            }
            consume(TokenType.UNARY_OPERATOR);
        }

        List<Object> results = L2();
        Object result = results.getFirst();
        if (signChange == 0 )
            return results;

        System.out.println(result);
        Object modifiedValue = GeneralUtils.multiplyNumbers(result, signChange);
        results.set(0, modifiedValue);
        return results;
    }
//  L4: Multiplication and division. Have higher priority than addition.
    private List<Object> L4() {
        List<Object> results = L3();
        Object result = results.getFirst();

        while (currentToken.getValue().equals("*") || currentToken.getValue().equals("/")) {
            if (currentToken.getValue().equals("*")) {
                consume(TokenType.BINARY_OPERATOR);
                List<Object> rights = L3();
                if (rights.isEmpty())
                    throw new RuntimeException("Missing tokens on the rhl: " + rights);
                Object right = rights.getFirst();
                result = GeneralUtils.multiplyNumbers(result, right);
                results.set(0, result);

            } else if (currentToken.getValue().equals("/")) {
                consume(TokenType.BINARY_OPERATOR);
                List<Object> rights = L3();
                if (rights.isEmpty())
                    throw new RuntimeException("Missing tokens on the rhl: " + rights);
                Object right = rights.getFirst();
                result = GeneralUtils.divideNumbers(result, right);
                results.set(0, result);

            }
        }
        return results;
    }
//  L5: Addition and subtraction.
    private List<Object> L5() {
        List<Object> results = L4();

        if(results.isEmpty())
            throw new RuntimeException("No return value: " + results);
        Object result = results.getFirst();

        while (currentToken.getValue().equals("+") || currentToken.getValue().equals("-")) {
            if (currentToken.getValue().equals("+")) {
                consume(TokenType.BINARY_OPERATOR);
                List<Object> rights = L4();
                if (rights.isEmpty())
                    throw new RuntimeException("Missing tokens on the rhl: " + rights);
                Object right = rights.getFirst();
                result = GeneralUtils.addNumbers(result, right);
                results.set(0, result);

            } else if (currentToken.getValue().equals("-")) {
                consume(TokenType.BINARY_OPERATOR);
                List<Object> rights = L4();
                if (rights.isEmpty())
                        throw new RuntimeException("Missing tokens on the rhl: " + rights);
                Object right = rights.getFirst();
                result = GeneralUtils.subNumbers(result, right);
                results.set(0, result);

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
//      Morever for the scalability where complex functions could return multiple values...
        if(results.size() != 1)
            throw new RuntimeException("Returned more than one value: ");
        System.out.println("Parser result: >" + results.getFirst().toString() + "<");
        return results.getFirst();
    }
}
