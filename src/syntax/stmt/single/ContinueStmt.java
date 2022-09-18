package syntax.stmt.single;

import lexer.token.Token;

public class ContinueStmt extends SingleStmt {
    private Token continueSym;
    
    public ContinueStmt(Token continueSym, Token semicolon) {
        super(semicolon);
        this.continueSym = continueSym;
    }
    
    @Override
    public String toString() {
        return continueSym.toString() + super.toString();
    }
}
