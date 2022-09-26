package syntax.exp.unary;

import lexer.token.Ident;
import syntax.SyntaxNode;

import java.util.LinkedList;

public interface ExpUnit extends SyntaxNode {
    LinkedList<Ident> getNames();
}
