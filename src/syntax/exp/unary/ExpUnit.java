package syntax.exp.unary;

import lexer.token.Ident;

import java.util.LinkedList;

public interface ExpUnit {
    LinkedList<Ident> getNames();
}
