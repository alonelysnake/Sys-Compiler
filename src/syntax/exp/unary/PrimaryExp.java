package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.Ident;
import middle.BlockInfo;
import middle.MiddleState;
import symbol.SymTable;

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
    
    @Override
    public int getMaxLine() {
        return unit.getMaxLine();
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
    
    public int calConst(SymTable symTable) {
        return unit.calConst(symTable);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        return unit.generateIcode(state);
    }
    
    @Override
    public String toString() {
        return unit.toString() + "<PrimaryExp>\n";
    }
}
