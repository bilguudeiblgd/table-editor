package parser.functions;

import parser.utils.GeneralUtils;

public class SumFunction extends Function {

    @Override
    public Object calculate() {
        if (arguments.isEmpty())
            throw new IllegalArgumentException("Argument empty.");
        Object sum = arguments.getFirst();
        for (Object arg : arguments) {
            sum = GeneralUtils.addNumbers(sum, arg);
        }
//      Counted 1 extra.
        sum = GeneralUtils.subNumbers(sum, arguments.getFirst());
        return sum;
    }
}