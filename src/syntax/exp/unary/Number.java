package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.IntConst;

public class Number implements PrimaryUnit {
    private final IntConst strVal;
    
    public Number(IntConst strVal) {
        this.strVal = strVal;
    }
    
    @Override
    public void analyse(AnalysisState state) {
    
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
