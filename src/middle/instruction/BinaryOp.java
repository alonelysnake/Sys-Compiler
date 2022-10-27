package middle.instruction;

import middle.val.Number;
import middle.val.Value;

public class BinaryOp extends INode implements StackSpace {
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
        LE("≤"),
        SLL("<<");//中间代码不生成，翻译mips时用
        
        private final String str;
        
        Operator(String s) {
            this.str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    public static int calConst(BinaryOp iCode) {
        Operator op = iCode.op;
        int first = ((Number) iCode.op1).getVal();
        int second = ((Number) iCode.op2).getVal();
        switch (op) {
            case ADD:
                return first + second;
            case SUB:
                return first - second;
            case MULT:
                return first * second;
            case DIV:
                return first / second;
            case MOD:
                return first % second;
            case AND:
                if (first == 0 || second == 0) {
                    return 0;
                } else {
                    return 1;
                }
            case OR:
                if (first == 0 && second == 0) {
                    return 0;
                } else {
                    return 1;
                }
            case EQ:
                if (first != second) {
                    return 0;
                } else {
                    return 1;
                }
            case NEQ:
                if (first == second) {
                    return 0;
                } else {
                    return 1;
                }
            case GE:
                if (first < second) {
                    return 0;
                } else {
                    return 1;
                }
            case GT:
                if (first <= second) {
                    return 0;
                } else {
                    return 1;
                }
            case LE:
                if (first > second) {
                    return 0;
                } else {
                    return 1;
                }
            case LT:
                if (first >= second) {
                    return 0;
                } else {
                    return 1;
                }
            case SLL:
                return first << second;
        }
        return 0;
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
    public int getSize() {
        if (result.isTemp()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public Value getNewVar() {
        return result;
    }
    
    public Operator getOp() {
        return op;
    }
    
    public Value getOp1() {
        return op1;
    }
    
    public Value getOp2() {
        return op2;
    }
    
    public Value getResult() {
        return result;
    }
    
    @Override
    public String toString() {
        return result + " = " + op1 + " " + op + " " + op2 + "\n";
    }
}
