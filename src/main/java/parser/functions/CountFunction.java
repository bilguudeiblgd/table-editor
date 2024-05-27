package parser.functions;

public class CountFunction extends Function {
    @Override
    public Object calculate() {
        return arguments.size();
    }
}
