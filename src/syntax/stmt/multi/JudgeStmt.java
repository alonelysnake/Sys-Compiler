package syntax.stmt.multi;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class JudgeStmt implements MultiStmt {
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
    
    @Override
    public void analyse(AnalysisState state) {
        condExp.analyse(state);
        if (rightParent == null) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));//TODO 行数修改
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
