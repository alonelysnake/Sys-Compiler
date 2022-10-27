package middle.instruction;

import middle.val.Value;

public class UnaryOp extends INode implements StackSpace {
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
    
    public Value getSrc() {
        return src;
    }
    
    public Value getDst() {
        return dst;
    }
    
    public Operator getOp() {
        return op;
    }
    
    @Override
    public int getSize() {
        if (dst.isTemp()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public Value getNewVar() {
        return dst;
    }
    
    @Override
    public String toString() {
        return dst + " = " + op + " " + src + "\n";
    }
}
