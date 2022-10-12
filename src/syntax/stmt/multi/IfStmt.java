package syntax.stmt.multi;

import error.AnalysisState;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class IfStmt extends JudgeStmt {
    private final Token ifSym;
    private final Token elseSym;
    private final Stmt elseStmt;
    
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
    public void analyse(AnalysisState state) {
        super.analyse(state);
        if (elseStmt != null) {
            elseStmt.analyse(state);
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
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
