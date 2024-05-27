package parser.functions;

public class MaxFunction extends Function {
    @Override
    public Object calculate() {
        if (arguments.isEmpty())
            throw new IllegalArgumentException("Argument empty.");
        double value = Double.MIN_VALUE;
        if (arguments.getFirst() instanceof Number) {
            value = ((Number) arguments.getFirst()).doubleValue();
        }
        for (Object arg : arguments) {
            if (arg instanceof Number) {
                value = Math.max(((Number) arg).doubleValue(), value);
            } else {
                throw new IllegalArgumentException("Invalid argument type for SumFunction. Expected Number.");
            }
        }
        return value;
    }
}
