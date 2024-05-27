package parser.functions;

public class CountFunction extends Function {
    @Override
    public Object calculate() {
        int count = 0;
        for(Object arg : this.arguments) {
            if(!arg.toString().isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
