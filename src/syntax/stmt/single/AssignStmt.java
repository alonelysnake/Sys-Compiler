package syntax.stmt.single;

import lexer.token.Token;
import syntax.exp.multi.Exp;
import syntax.exp.unary.LVal;

public class AssignStmt extends SingleStmt {
    private final LVal lval;
    private final Token assign;
    private final Exp exp;
    
    public AssignStmt(LVal lval, Token assign, Exp exp, Token semicolon) {
        super(semicolon);
        this.lval = lval;
        this.assign = assign;
        this.exp = exp;
    }
    
    public LVal getLval() {
        return lval;
    }
    
    @Override
    public String toString() {
        return lval.toString() + assign.toString() + exp.toString() + super.toString();
    }
}
