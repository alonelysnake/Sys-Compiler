package syntax.exp.multi;

import lexer.token.Token;

import java.util.LinkedList;

public class EqExp extends ExpList<RelExp> {
    public EqExp(LinkedList<Token> ops, LinkedList<RelExp> units) {
        super(ops, units, "<EqExp>\n");
    }
}
