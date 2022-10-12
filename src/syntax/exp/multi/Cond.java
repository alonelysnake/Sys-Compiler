package syntax.exp.multi;

import error.AnalysisState;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.SyntaxNode;

public class Cond implements SyntaxNode {
    private final LOrExp exp;
    
    public Cond(LOrExp exp) {
        this.exp = exp;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        exp.analyse(state);
    }
    
    public int getMaxLine() {
        return exp.getMaxLine();
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Cond>\n";
    }
}
