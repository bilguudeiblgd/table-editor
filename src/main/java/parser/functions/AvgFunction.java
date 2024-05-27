package parser.functions;

public class AvgFunction extends Function {

    @Override
    public Object calculate() {
        double sum = 0;
        if (arguments.isEmpty())
            throw new IllegalArgumentException("Argument empty.");

        for (Object arg : arguments) {
            if (arg instanceof Number) {
                sum += ((Number) arg).doubleValue();
            } else {
                throw new IllegalArgumentException("Invalid argument type for AvgFunction. Expected Number.");
            }
        }
        sum /= arguments.size();
        return sum;
    }
}