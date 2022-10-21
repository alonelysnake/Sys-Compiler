package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;

import java.util.LinkedList;

public class LOrExp extends ExpList<AndExp> {
    public LOrExp(LinkedList<Token> ops, LinkedList<AndExp> units) {
        super(ops, units, "<LOrExp>\n");
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
