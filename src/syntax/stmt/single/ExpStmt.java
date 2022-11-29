package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.exp.multi.Exp;

public class ExpStmt extends SingleStmt {
    private final Exp exp;
    
    public ExpStmt(Exp exp, Token semicolon) {
        super(semicolon);
        this.exp = exp;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        exp.analyse(state);
        if (!hasSemicolon()) {
            state.addError(new Error(exp.getMaxLine(), ErrorType.LACK_SEMICOLON));
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        return exp.generateIcode(state);
    }
    
    @Override
    public String toString() {
        if (exp != null) {
            return exp + super.toString();
        } else {
            return super.toString();
        }
    }
}
