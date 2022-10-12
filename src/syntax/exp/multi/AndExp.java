package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;

import java.util.LinkedList;

public class AndExp extends ExpList<EqExp> {
    public AndExp(LinkedList<Token> ops, LinkedList<EqExp> units) {
        super(ops, units, "<LAndExp>\n");
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
}
