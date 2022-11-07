package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;

public class UnaryOp extends INode implements DefNode, UseNode {
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
    
    private Value src;
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
    public Value getDef() {
        return dst;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(src);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        if (!(src instanceof Number)) {
            src = ops.get(0);
        }
    }
    
    public INode optimize() {
        if (src instanceof Number) {
            if (op.equals(Operator.NEG)) {
                return new Move(dst, new Number(-((Number) src).getVal()));
            } else if (op.equals(Operator.NOT)) {
                if (((Number) src).getVal() == 0) {
                    return new Move(dst, new Number(1));
                } else {
                    return new Move(dst, new Number(0));
                }
            } else {
                System.err.println("unaryOp: optimize 非法符号");
            }
        }
        return this;
    }
    
    @Override
    public String toString() {
        return dst + " = " + op + " " + src + "\n";
    }
}
