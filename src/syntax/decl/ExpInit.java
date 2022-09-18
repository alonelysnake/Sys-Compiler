package syntax.decl;

import syntax.exp.multi.Exp;

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
    public String toString() {
        if (isConst()) {
            return exp + "<ConstInitVal>\n";
        } else {
            return exp + "<InitVal>\n";
        }
    }
}
