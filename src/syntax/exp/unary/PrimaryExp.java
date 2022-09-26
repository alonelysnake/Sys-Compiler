package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.Ident;

import java.util.LinkedList;

public class PrimaryExp implements ExpUnit {
    private final PrimaryUnit unit;
    
    public PrimaryExp(PrimaryUnit unit) {
        this.unit = unit;
    }
    
    public boolean isSubExp() {
        return unit instanceof SubExp;
    }
    
    public ExpUnit getFirstExpUnit() {
        return ((SubExp) unit).getFirstExpUnit();
    }
    
    public PrimaryUnit getUnit() {
        return unit;
    }
    
    public LinkedList<Ident> getNames() {
        if (unit instanceof LVal) {
            return ((LVal) unit).getNames();
        } else if (unit instanceof SubExp) {
            return ((SubExp) unit).getNames();
        } else {
            return new LinkedList<>();
        }
    }
    
    @Override
    public void analyse(AnalysisState state) {
        unit.analyse(state);
    }
    
    @Override
    public String toString() {
        return unit.toString() + "<PrimaryExp>\n";
    }
}
