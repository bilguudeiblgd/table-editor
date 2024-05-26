package parser.functions;

import java.util.ArrayList;
import java.util.List;

public class Function {
    List<Object> arguments;

    public Function() {
        this.arguments = new ArrayList<>();
    }

    public void addArgument(Object arg) {
        arguments.add(arg);
    }

    public void addArguments(List<Object> args) {
        arguments.addAll(args);
    }

    public Object calculate() {
        return null;
    }
}



