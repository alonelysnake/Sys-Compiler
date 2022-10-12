package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.IntConst;
import middle.BlockInfo;
import middle.MiddleState;
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
        //TODO
        return null;
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
