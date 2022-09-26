package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import symbol.SymTable;
import symbol.Symbol;
import syntax.exp.multi.Exp;
import syntax.exp.unary.LVal;

public class AssignStmt extends SingleStmt {
    private final LVal lval;
    private final Token assign;
    private final Exp exp;
    
    public AssignStmt(LVal lval, Token assign, Exp exp, Token semicolon) {
        super(semicolon);
        this.lval = lval;
        this.assign = assign;
        this.exp = exp;
    }
    
    public LVal getLval() {
        return lval;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        // 左值修改
        String name = lval.getName().getName();
        Symbol symbol = symTable.get(name);
        if (symbol != null) {
            if (symbol.isConst()) {
                state.addError(new Error(lval.getName().getLine(), ErrorType.MODIFY_CONST));//TODO 行数修改
            }
        }
        lval.analyse(state);
        exp.analyse(state);
        if (!hasSemicolon()) {
            state.addError(new Error(assign.getLine(), ErrorType.LACK_SEMICOLON));//TODO 行数修改
        }
    }
    
    @Override
    public String toString() {
        return lval.toString() + assign.toString() + exp.toString() + super.toString();
    }
}
