package backend.instruction;

import backend.element.Reg;
import middle.instruction.BinaryOp;

import java.util.HashMap;

public class RCal extends MIPSCode {
    //R类型计算指令，包括addu subu mul div rem(余数) and or sgt sge slt sle seq sne
    
    public enum Op {
        ADDU, SUBU, MUL, DIV, REM, AND, OR, SGT, SGE, SLT, SLE, SEQ, SNE;
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    public static final HashMap<BinaryOp.Operator, Op> middle2MIPSBinary = new HashMap<BinaryOp.Operator, Op>() {
        {
            put(BinaryOp.Operator.ADD, Op.ADDU);
            put(BinaryOp.Operator.SUB, Op.SUBU);
            put(BinaryOp.Operator.MULT, Op.MUL);
            put(BinaryOp.Operator.DIV, Op.DIV);
            put(BinaryOp.Operator.MOD, Op.REM);
            put(BinaryOp.Operator.AND, Op.AND);
            put(BinaryOp.Operator.OR, Op.OR);
            put(BinaryOp.Operator.GT, Op.SGT);
            put(BinaryOp.Operator.GE, Op.SGE);
            put(BinaryOp.Operator.LT, Op.SLT);
            put(BinaryOp.Operator.LE, Op.SLE);
            put(BinaryOp.Operator.EQ, Op.SEQ);
            put(BinaryOp.Operator.NEQ, Op.SNE);
        }
    };
    
    private final Op op;
    private final Reg left;
    private final Reg reg1;
    private final Reg reg2;
    
    public RCal(Op op, Reg left, Reg reg1, Reg reg2) {
        this.op = op;
        this.left = left;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }
    
    @Override
    public String toString() {
        return op + " " + left + ", " + reg1 + ", " + reg2 + "\n";
    }
}
