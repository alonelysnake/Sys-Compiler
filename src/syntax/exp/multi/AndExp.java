package syntax.exp.multi;

import lexer.token.Token;

import java.util.LinkedList;

public class AndExp extends ExpList<EqExp> {
    public AndExp(LinkedList<Token> ops, LinkedList<EqExp> units) {
        super(ops, units, "<LAndExp>\n");
    }
}
