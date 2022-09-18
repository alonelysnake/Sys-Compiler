package syntax.stmt.single;

import lexer.token.Token;
import syntax.exp.multi.Exp;

public class ExpStmt extends SingleStmt {
    private final Exp exp;
    
    public ExpStmt(Exp exp, Token semicolon) {
        super(semicolon);
        this.exp = exp;
    }
    
    @Override
    public String toString() {
        if (exp != null) {
            return exp + super.toString();
        } else {
            return super.toString();
        }
    }
}
