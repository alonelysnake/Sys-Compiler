package syntax.exp.multi;

import error.AnalysisState;

public class Cond {
    private final LOrExp exp;
    
    public Cond(LOrExp exp) {
        this.exp = exp;
    }
    
    public void analyse(AnalysisState state) {
        exp.analyse(state);
    }
    
    public int getMaxLine() {
        return exp.getMaxLine();
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Cond>\n";
    }
}
