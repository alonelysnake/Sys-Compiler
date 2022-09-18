package syntax.exp.multi;

import lexer.token.Token;
import syntax.exp.unary.LVal;

import java.util.LinkedList;

public class AddExp extends ExpList<MulExp> {
    public AddExp(LinkedList<Token> ops, LinkedList<MulExp> units) {
        super(ops, units, "<AddExp>\n");
    }
    
    public LVal getLVal() {
        if (size() != 1) {
            return null;
        }
        return getFirst().getLVal();
    }
}
