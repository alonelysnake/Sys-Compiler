package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import syntax.exp.unary.ExpUnit;

import java.util.LinkedList;

public class Exp {
    private final AddExp exp;
    
    public Exp(AddExp exp) {
        this.exp = exp;
    }
    
    public AddExp getExp() {
        return exp;
    }
    
    public ExpUnit getFirstExpUnit() {
        return exp.getFirstExpUnit();
    }
    
    public LinkedList<Ident> getNames() {
        return exp.getNames();
    }
    
    public void analyse(AnalysisState state) {
        exp.analyse(state);
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Exp>\n";
    }
}
