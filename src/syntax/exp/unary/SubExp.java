package syntax.exp.unary;

import lexer.token.Token;
import syntax.exp.multi.Exp;

public class SubExp implements PrimaryUnit {
    /**
     * '(' + exp + ')'
     */
    
    private Token leftParent;
    private Exp exp;
    private Token rightParent;
    
    public SubExp(Token leftParent, Exp exp, Token rightParent) {
        this.leftParent = leftParent;
        this.exp = exp;
        this.rightParent = rightParent;
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
