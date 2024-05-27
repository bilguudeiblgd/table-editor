package parser.functions;

import java.util.MissingFormatArgumentException;

public class PowFunction extends Function {
//  #TODO: adapt Object way
    @Override
    public Object calculate() {
        if(arguments.size() != 2) {
            throw new MissingFormatArgumentException("Argument not equal to two!, pow function");
        }

        Object baseObj = arguments.get(0);
        Object exponentObj = arguments.get(1);

        if (baseObj instanceof Number && exponentObj instanceof Number) {
            double base = ((Number) baseObj).doubleValue();
            int exponent = ((Number) exponentObj).intValue();
            return pow(base, exponent);
        } else {
            throw new IllegalArgumentException("Arguments must be numbers");
        }
    }
//    Using efficient way to find pow. Instead of multiplying by itself exponent times, we divide and conquer the exponents
    public static double pow(double base, int exponent) {
        // Handle the base case
        if (exponent == 0) {
            return 1;
        }
        boolean isNegativeExponent = exponent < 0;
        int absExponent = Math.abs(exponent);
        double result = powerHelper(base, absExponent);
        return isNegativeExponent ? 1.0 / result : result;
    }

    private static double powerHelper(double base, int exponent) {
        if (exponent == 0) {
            return 1;
        }
        double half = powerHelper(base, exponent / 2);

        if (exponent % 2 == 0) {
            return half * half;
        } else {
            return half * half * base;
        }
    }
}