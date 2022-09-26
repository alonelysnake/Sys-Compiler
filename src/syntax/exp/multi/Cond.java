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
    
    @Override
    public String toString() {
        return exp.toString() + "<Cond>\n";
    }
}
