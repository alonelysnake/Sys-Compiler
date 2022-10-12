package middle.instruction;

import middle.val.Value;

public class BinaryOp extends INode {
    //二元操作的动作单元
    //op1, op, op2, out(symbol)
    //输出格式：out = op1 op op2
    public enum Operator {
        ADD("+"),
        SUB("-"),
        MULT("×"),
        DIV("÷"),
        MOD("%"),
        AND("&&"),
        OR("||"),
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
    
    private final Value result;//通常存在一个临时变量里
    private final Operator op;
    private final Value op1;
    private final Value op2;
    
    public BinaryOp(Value result, Operator op, Value op1, Value op2) {
        this.result = result;
        this.op = op;
        this.op1 = op1;
        this.op2 = op2;
    }
    
    @Override
    public String toString() {
        return result + " = " + op1 + " " + op + " " + op2 + "\n";
    }
}
