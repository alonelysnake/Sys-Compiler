package syntax.stmt.multi;

import lexer.token.Token;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class IfStmt extends JudgeStmt {
    private Token ifSym;
    private Token elseSym;
    private Stmt elseStmt;
    
    public IfStmt(Token ifSym, Token leftParent, Cond condExp, Token rightParent, Stmt mainStmt,
                  Token elseSym, Stmt elseStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.ifSym = ifSym;
        this.elseSym = elseSym;
        this.elseStmt = elseStmt;
    }
    
    public IfStmt(Token ifSym, Token leftParent, Cond condExp, Token rightParent, Stmt mainStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.ifSym = ifSym;
        this.elseSym = null;
        this.elseStmt = null;
    }
    
    public boolean hasElse() {
        return this.elseSym != null;
    }
    
    @Override
    public String toString() {
        if (hasElse()) {
            return this.ifSym + super.toString() + this.elseSym + this.elseStmt;
        } else {
            return this.ifSym + super.toString();
        }
    }
}