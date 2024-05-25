package utils.functions;

import java.util.ArrayList;
import java.util.List;

public abstract class Function {
    protected List<Object> arguments;

    public Function() {
        this.arguments = new ArrayList<>();
    }

    public void addArgument(Object arg) {
        arguments.add(arg);
    }

    public void addArguments(List<Object> args) {
        arguments.addAll(args);
    }

    public abstract Object calculate();
}



