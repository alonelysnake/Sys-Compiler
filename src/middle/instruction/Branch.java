package middle.instruction;

import middle.val.Value;

public class Branch extends INode {
    //分支语句
    
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
    
    @Override
    public String toString() {
        return "if " + left + " " + op + " " + right + ", branch " + label + "\n";
    }
}
