package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;

public class BreakStmt extends SingleStmt {
    private final Token breakSym;
    
    public BreakStmt(Token breakSym, Token semicolon) {
        super(semicolon);
        this.breakSym = breakSym;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        if (!state.isInLoop()) {
            state.addError(new Error(breakSym.getLine(), ErrorType.BREAK_OR_CONTINUE_OUTSIDE_LOOP));
        }
        if (!hasSemicolon()) {
            state.addError(new Error(breakSym.getLine(), ErrorType.LACK_SEMICOLON));//break一定存在且为分号前一个
        }
    }
    
    @Override
    public String toString() {
        return breakSym.toString() + super.toString();
    }
}
