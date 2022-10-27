package backend.instruction;

import backend.element.Imm;
import backend.element.Reg;
import middle.instruction.BinaryOp;

import java.util.HashMap;

public class ICal extends MIPSCode {
    // I类型计算指令，包括 addiu subiu mul div rem andi ori sgt sge slti sle seq sne sll
    
    public enum Op {
        ADDIU, SUBIU, MUL, DIV, REM, ANDI, ORI, SGT, SGE, SLTI, SLE, SEQ, SNE, SLL;
        
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    public static final HashMap<BinaryOp.Operator, Op> middle2MIPSBinary = new HashMap<BinaryOp.Operator, Op>() {
        {
            put(BinaryOp.Operator.ADD, Op.ADDIU);
            put(BinaryOp.Operator.SUB, Op.SUBIU);
            put(BinaryOp.Operator.MULT, Op.MUL);
            put(BinaryOp.Operator.DIV, Op.DIV);
            put(BinaryOp.Operator.MOD, Op.REM);
            put(BinaryOp.Operator.AND, Op.ANDI);
            put(BinaryOp.Operator.OR, Op.ORI);
            put(BinaryOp.Operator.GT, Op.SGT);
            put(BinaryOp.Operator.GE, Op.SGE);
            put(BinaryOp.Operator.LT, Op.SLTI);
            put(BinaryOp.Operator.LE, Op.SLE);
            put(BinaryOp.Operator.EQ, Op.SEQ);
            put(BinaryOp.Operator.NEQ, Op.SNE);
            put(BinaryOp.Operator.SLL, Op.SLL);
        }
    };
    
    private final Op op;
    private final Reg left;
    private final Reg reg;
    private final Imm imm;
    
    public ICal(Op op, Reg left, Reg reg, Imm imm) {
        this.op = op;
        this.left = left;
        this.reg = reg;
        this.imm = imm;
    }
    
    @Override
    public String toString() {
        return op + " " + left + ", " + reg + ", " + imm + "\n";
    }
}
