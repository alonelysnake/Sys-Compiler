package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.IntConst;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.Nop;
import symbol.SymTable;

public class Number implements PrimaryUnit {
    private final IntConst strVal;
    
    public Number(IntConst strVal) {
        this.strVal = strVal;
    }
    
    @Override
    public void analyse(AnalysisState state) {
    
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO 是否要设置成 t1 = 0 用临时寄存器存储的形式?
        INode first = new Nop();
        return new BlockInfo(new middle.val.Number(strVal.getVal()), first, first);
    }
    
    public int calConst(SymTable symTable) {
        return strVal.getVal();
    }
    
    @Override
    public int getMaxLine() {
        return strVal.getLine();
    }
    
    @Override
    public String toString() {
        return strVal + "<Number>\n";
    }
}
