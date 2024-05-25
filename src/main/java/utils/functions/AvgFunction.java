package utils.functions;

public class AvgFunction extends Function {

    @Override
    public Object calculate() {
        double sum = 0;
        for (Object arg : arguments) {
            if (arg instanceof Number) {
                sum += ((Number) arg).doubleValue();
            } else {
                throw new IllegalArgumentException("Invalid argument type for SumFunction. Expected Number.");
            }
        }
        sum /= arguments.size();
        return sum;
    }
}