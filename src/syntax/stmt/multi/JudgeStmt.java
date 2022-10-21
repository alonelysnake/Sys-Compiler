package syntax.stmt.multi;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public abstract class JudgeStmt implements MultiStmt {
    private final Token leftParent;
    private final Cond condExp;
    private final Token rightParent;
    private final Stmt mainStmt;
    
    public JudgeStmt(Token leftParent, Cond condExp, Token rightParent, Stmt mainStmt) {
        this.leftParent = leftParent;
        this.condExp = condExp;
        this.rightParent = rightParent;
        this.mainStmt = mainStmt;
    }
    
    public Cond getCondExp() {
        return condExp;
    }
    
    public Stmt getMainStmt() {
        return mainStmt;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        condExp.analyse(state);
        if (rightParent == null) {
            state.addError(new Error(condExp.getMaxLine(), ErrorType.LACK_R_PARENT));
        }
        mainStmt.analyse(state);
    }
    
    @Override
    public String toString() {
        return String.valueOf(leftParent) +
                condExp +
                rightParent +
                mainStmt;
    }
}
