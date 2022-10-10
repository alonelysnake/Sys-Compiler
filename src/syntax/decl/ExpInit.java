package syntax.decl;

import error.AnalysisState;
import syntax.exp.multi.Exp;

import java.util.ArrayList;

public class ExpInit implements InitVal {
    private final boolean constFlag;
    private final Exp exp;
    
    public ExpInit(boolean constFlag, Exp exp) {
        this.constFlag = constFlag;
        this.exp = exp;
    }
    
    @Override
    public boolean isConst() {
        return constFlag;
    }
    
    @Override
    public int getMaxLine() {
        return exp.getMaxLine();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        exp.analyse(state);
    }
    
    @Override
    public ArrayList<Exp> getInitVals() {
        ArrayList<Exp> ret = new ArrayList<>();
        ret.add(exp);
        return ret;
    }
    
    @Override
    public String toString() {
        if (isConst()) {
            return exp + "<ConstInitVal>\n";
        } else {
            return exp + "<InitVal>\n";
        }
    }
}
