package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;
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
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
