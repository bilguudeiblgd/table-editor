package parser.functions;

public class ConcatFunction extends Function {
    @Override
    public Object calculate() {
        StringBuilder result = new StringBuilder();
        for (Object argument : arguments) {
            result.append(argument.toString());
        }
        return result.toString();
    }
}
