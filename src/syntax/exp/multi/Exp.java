package syntax.exp.multi;

import syntax.exp.unary.LVal;

public class Exp {
    private AddExp exp;
    
    public Exp(AddExp exp) {
        this.exp = exp;
    }
    
    public AddExp getExp() {
        return exp;
    }
    
    public LVal getLVal() {
        return exp.getLVal();
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Exp>\n";
    }
}
