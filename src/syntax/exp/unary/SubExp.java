package syntax.exp.unary;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.LinkedList;

public class SubExp implements PrimaryUnit {
    /**
     * '(' + exp + ')'
     */
    
    private final Token leftParent;
    private final Exp exp;
    private final Token rightParent;
    
    public SubExp(Token leftParent, Exp exp, Token rightParent) {
        this.leftParent = leftParent;
        this.exp = exp;
        this.rightParent = rightParent;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    public LinkedList<Ident> getNames() {
        return exp.getNames();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (leftParent != null) {
            sb.append(leftParent);
        }
        sb.append(exp);
        if (rightParent != null) {
            sb.append(rightParent);
        }
        return sb.toString();
    }
}
