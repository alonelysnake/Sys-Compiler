package syntax.stmt.multi;

import lexer.token.Token;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class WhileStmt extends JudgeStmt {
    private final Token whileSym;
    
    public WhileStmt(Token whileSym, Token leftParent, Cond condExp, Token rightParent,
                     Stmt mainStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.whileSym = whileSym;
    }
    
    @Override
    public String toString() {
        return this.whileSym + super.toString();
    }
}
