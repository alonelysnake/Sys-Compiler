package syntax.exp.multi;

import lexer.token.Ident;
import syntax.exp.unary.LVal;

import java.util.LinkedList;

public class Exp {
    private final AddExp exp;
    
    public Exp(AddExp exp) {
        this.exp = exp;
    }
    
    public AddExp getExp() {
        return exp;
    }
    
    public LVal getLVal() {
        return exp.getLVal();
    }
    
    public LinkedList<Ident> getNames() {
        return exp.getNames();
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Exp>\n";
    }
}
