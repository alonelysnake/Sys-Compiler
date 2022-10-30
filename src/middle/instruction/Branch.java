package middle.instruction;

import middle.val.Value;

public class Branch extends INode {
    //分支语句
    //在中间代码中规定right恒为0
    // if left <op> right, then jump to label
    public enum Operator {
        EQ("=="),
        NEQ("!="),
        GT(">"),
        GE("≥"),
        LT("<"),
        LE("≤");
        
        private final String str;
        
        Operator(String s) {
            this.str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    private final Value left;//分支语句判断的左操作数
    private final Value right;//右操作数
    private final Operator op;
    private final String label;//满足情况时的跳转标签
    
    public Branch(Value left, Value right, Operator op, String label) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.label = label;
    }
    
    public Value getLeft() {
        return left;
    }
    
    public Value getRight() {
        return right;
    }
    
    public Operator getOp() {
        return op;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public String toString() {
        return "if " + left + " " + op + " " + right + ", branch " + label + "\n";
    }
}
