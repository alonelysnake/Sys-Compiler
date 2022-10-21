package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;

import java.util.LinkedList;

public class EqExp extends ExpList<RelExp> {
    public EqExp(LinkedList<Token> ops, LinkedList<RelExp> units) {
        super(ops, units, "<EqExp>\n");
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
