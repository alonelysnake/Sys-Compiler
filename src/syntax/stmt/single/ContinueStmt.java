package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;

public class ContinueStmt extends SingleStmt {
    private final Token continueSym;
    
    public ContinueStmt(Token continueSym, Token semicolon) {
        super(semicolon);
        this.continueSym = continueSym;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        if (!state.isInLoop()) {
            state.addError(new Error(continueSym.getLine(), ErrorType.BREAK_OR_CONTINUE_OUTSIDE_LOOP));
        }
        if (!hasSemicolon()) {
            state.addError(new Error(continueSym.getLine(), ErrorType.LACK_SEMICOLON));
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    @Override
    public String toString() {
        return continueSym.toString() + super.toString();
    }
}
