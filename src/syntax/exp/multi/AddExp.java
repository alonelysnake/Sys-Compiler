package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import syntax.decl.BType;
import syntax.exp.unary.ExpUnit;

import java.util.LinkedList;

public class AddExp extends ExpList<MulExp> {
    public AddExp(LinkedList<Token> ops, LinkedList<MulExp> units) {
        super(ops, units, "<AddExp>\n");
    }
    
    public ExpUnit getFirstExpUnit() {
        if (size() != 1) {
            return null;
        }
        return getFirst().getFirstExpUnit();
    }
    
    public BType getExpType(AnalysisState state) {
        if (size() == 1) {
            return getFirst().getExpType(state);
        }
        BType type = getFirst().getExpType(state);
        for (MulExp unit : getUnits()) {
            BType next = unit.getExpType(state);
            if (type != next) {
                return null;
            }
        }
        if (type == BType.VOID) {
            return null;
        }
        return type;
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
