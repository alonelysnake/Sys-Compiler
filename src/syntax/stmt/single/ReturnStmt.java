package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import lexer.token.TokenCategory;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.exp.multi.Exp;

public class ReturnStmt extends SingleStmt {
    private final Token returnSym;
    private final Exp exp;
    
    public ReturnStmt(Token returnSym, Exp exp, Token semicolon) {
        super(semicolon);
        this.returnSym = returnSym;
        this.exp = exp;
    }
    
    public ReturnStmt(Token returnSym, Token semicolon) {
        super(semicolon);
        this.returnSym = returnSym;
        this.exp = null;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //判断函数是否缺少返回值
        //保证全局的Decl部分不会出现return语句
        //如果state的curFunc不为空说明在自定义函数里，根据其functype判断。为空说明在main函数里，一定要有return
        if (state.getCurFunc() != null && state.getCurFunc().getType().equals(TokenCategory.VOID)) {
            if (exp != null) {
                state.addError(new Error(returnSym.getLine(), ErrorType.MISMATCH_RETURN));
            }
        }
        //判断分号
        if (!hasSemicolon()) {
            if (exp == null) {
                state.addError(new Error(returnSym.getLine(), ErrorType.LACK_SEMICOLON));
            } else {
                state.addError(new Error(exp.getMaxLine(), ErrorType.LACK_SEMICOLON));
            }
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnSym);
        if (exp != null) {
            sb.append(exp);
        }
        sb.append(getSemicolon());
        return sb.toString();
    }
}
