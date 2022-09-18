package syntax.stmt.multi;

import lexer.token.Token;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class JudgeStmt implements MultiStmt {
    private Token laftParent;
    private Cond condExp;
    private Token rightParent;
    private Stmt mainStmt;
    
    public JudgeStmt(Token laftParent, Cond condExp, Token rightParent, Stmt mainStmt) {
        this.laftParent = laftParent;
        this.condExp = condExp;
        this.rightParent = rightParent;
        this.mainStmt = mainStmt;
    }
    
    @Override
    public String toString() {
        return String.valueOf(laftParent) +
                condExp +
                rightParent +
                mainStmt;
    }
}
