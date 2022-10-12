package middle.instruction;

import middle.val.Value;

public class UnaryOp extends INode {
    public enum Operator {
        NEG("-"), NOT("!");
        
        private final String str;
        
        Operator(String s) {
            this.str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    private final Value src;
    private final Operator op;
    private final Value dst;
    
    public UnaryOp(Value src, Operator op, Value dst) {
        this.src = src;
        this.op = op;
        this.dst = dst;
    }
    
    @Override
    public String toString() {
        return dst + " = " + op + " " + src + "\n";
    }
}
