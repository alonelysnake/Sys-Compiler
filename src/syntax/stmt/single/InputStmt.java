package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import symbol.SymTable;
import symbol.Symbol;
import syntax.exp.unary.LVal;

public class InputStmt extends SingleStmt {
    private final LVal lVal;
    private final Token assign;
    private final Token name;
    private final Token leftParent;
    private final Token rightParent;
    
    public InputStmt(LVal lVal, Token assign,
                     Token name, Token leftParent, Token rightParent, Token semicolon) {
        super(semicolon);
        this.lVal = lVal;
        this.assign = assign;
        this.name = name;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
    }
    
    public LVal getlVal() {
        return lVal;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        // 左值修改
        String name = lVal.getName().getName();
        Symbol symbol = symTable.get(name);
        if (symbol != null) {
            if (symbol.isConst()) {
                state.addError(new Error(lVal.getName().getLine(), ErrorType.MODIFY_CONST));
            }
        }
        lVal.analyse(state);
        if (rightParent == null) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));//TODO 行数修改
        }
        if (!hasSemicolon()) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_SEMICOLON));//TODO 行数修改
        }
    }
    
    @Override
    public String toString() {
        return lVal.toString() +
                assign +
                name +
                leftParent +
                rightParent +
                getSemicolon();
    }
}
