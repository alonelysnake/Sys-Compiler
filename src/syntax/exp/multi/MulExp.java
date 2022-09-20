package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.unary.LVal;
import syntax.exp.unary.UnaryExp;

import java.util.LinkedList;

public class MulExp extends ExpList<UnaryExp> {
    public MulExp(LinkedList<Token> ops, LinkedList<UnaryExp> units) {
        super(ops, units, "<MulExp>\n");
    }
    
    public LVal getLVal() {
        if (size() != 1) {
            return null;
        }
        return getFirst().getLVal();
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
