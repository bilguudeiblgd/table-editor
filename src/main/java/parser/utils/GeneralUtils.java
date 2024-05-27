package parser.utils;

public class GeneralUtils {

    public static Object addNumbers(Object o1, Object o2) {
        Number a = toNumber(o1);
        Number b = toNumber(o2);

        if (a == null || b == null) {
            throw new ClassCastException("Cannot add numbers to " + o1 + " and " + o2);
        }

        if (a instanceof Float || b instanceof Float || a instanceof Double || b instanceof Double) {
            return a.doubleValue() + b.doubleValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() + b.longValue();
        } else {
            return a.intValue() + b.intValue();
        }
    }

    public static Object subNumbers(Object o1, Object o2) {
        Number a = toNumber(o1);
        Number b = toNumber(o2);

        if (a == null || b == null) {
            throw new ClassCastException("Cannot subtract numbers from " + o1 + " and " + o2);
        }

        if (a instanceof Float || b instanceof Float || a instanceof Double || b instanceof Double) {
            return a.doubleValue() - b.doubleValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() - b.longValue();
        } else {
            return a.intValue() - b.intValue();
        }
    }

    public static Object multiplyNumbers(Object o1, Object o2) {
        Number a = toNumber(o1);
        Number b = toNumber(o2);

        if (a == null || b == null) {
            throw new ClassCastException("Cannot multiply numbers " + o1 + " and " + o2);
        }

        if (a instanceof Float || b instanceof Float || a instanceof Double || b instanceof Double) {
            return a.doubleValue() * b.doubleValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() * b.longValue();
        } else {
            return a.intValue() * b.intValue();
        }
    }

    public static Object divideNumbers(Object o1, Object o2) {
        Number a = toNumber(o1);
        Number b = toNumber(o2);

        if (a == null || b == null) {
            throw new ClassCastException("Cannot divide numbers " + o1 + " and " + o2);
        }

        if (a instanceof Float || b instanceof Float || a instanceof Double || b instanceof Double) {
            return a.doubleValue() / b.doubleValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() / b.longValue();
        } else {
            return a.intValue() / b.intValue();
        }
    }

    private static Number toNumber(Object obj) {
        if (obj instanceof Number) {
            return (Number) obj;
        } else if (obj instanceof String) {
            String str = (String) obj;
            try {
                if (str.contains(".") || str.contains("e") || str.contains("E")) {
                    return Double.parseDouble(str);
                } else {
                    return Integer.parseInt(str);
                }
            } catch (NumberFormatException e) {
                System.out.println("Cannot parse " + obj + " to a number");
                return null;
            }
        }
        return null;
    }
}
