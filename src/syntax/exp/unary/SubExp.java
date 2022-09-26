package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.LinkedList;

public class SubExp implements PrimaryUnit {
    /**
     * '(' + exp + ')'
     */
    
    private final Token leftParent;
    private final Exp exp;
    private final Token rightParent;
    
    public SubExp(Token leftParent, Exp exp, Token rightParent) {
        this.leftParent = leftParent;
        this.exp = exp;
        this.rightParent = rightParent;
    }
    
    public Exp getExp() {
        return exp;
    }
    
    public LinkedList<Ident> getNames() {
        return exp.getNames();
    }
    
    public ExpUnit getFirstExpUnit() {
        return exp.getFirstExpUnit();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //子exp格式检查
        exp.analyse(state);
        //括号匹配检查
        if (rightParent == null) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));//TODO 行数修改
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (leftParent != null) {
            sb.append(leftParent);
        }
        sb.append(exp);
        if (rightParent != null) {
            sb.append(rightParent);
        }
        return sb.toString();
    }
}
