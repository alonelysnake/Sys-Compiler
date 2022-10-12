package syntax.stmt.multi;

import error.AnalysisState;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
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
    public void analyse(AnalysisState state) {
        state.getInLoop();
        super.analyse(state);
        state.getOutLoop();
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    @Override
    public String toString() {
        return this.whileSym + super.toString();
    }
}
