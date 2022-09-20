package syntax.exp.unary;

import lexer.token.Ident;

import java.util.LinkedList;

public class PrimaryExp implements ExpUnit {
    private final PrimaryUnit unit;
    
    public PrimaryExp(PrimaryUnit unit) {
        this.unit = unit;
    }
    
    public LVal getLVal() {
        if (unit instanceof LVal) {
            return (LVal) unit;
        }
        return null;
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
    public String toString() {
        return unit.toString() + "<PrimaryExp>\n";
    }
}
