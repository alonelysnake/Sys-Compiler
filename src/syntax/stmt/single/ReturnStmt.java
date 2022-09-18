package syntax.stmt.single;

import lexer.token.Token;
import syntax.exp.multi.Exp;

public class ReturnStmt extends SingleStmt {
    private Token returnSym;
    private Exp exp;
    
    public ReturnStmt(Token returnSym, Exp exp, Token semicolon) {
        super(semicolon);
        this.returnSym = returnSym;
        this.exp = exp;
    }
    
    public ReturnStmt(Token returnSym, Token semicolon) {
        super(semicolon);
        this.returnSym = returnSym;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnSym);
        if (exp != null) {
            sb.append(exp);
        }
        sb.append(getSemicolon());
        return sb.toString();
    }
}
