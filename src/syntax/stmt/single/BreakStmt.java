package syntax.stmt.single;

import lexer.token.Token;

public class BreakStmt extends SingleStmt {
    private final Token breakSym;
    
    public BreakStmt(Token breakSym, Token semicolon) {
        super(semicolon);
        this.breakSym = breakSym;
    }
    
    @Override
    public String toString() {
        return breakSym.toString() + super.toString();
    }
}
