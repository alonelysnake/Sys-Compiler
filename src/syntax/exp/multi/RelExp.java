package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;

import java.util.LinkedList;

public class RelExp extends ExpList<AddExp> {
    public RelExp(LinkedList<Token> ops, LinkedList<AddExp> units) {
        super(ops, units, "<RelExp>\n");
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
}
