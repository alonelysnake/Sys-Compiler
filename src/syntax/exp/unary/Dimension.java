package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.LinkedList;

public class Dimension {
    /**
     * 数组维度
     * '[' + exp/constexp + ']'
     */
    private final Token leftBracket;
    private final Exp exp;
    private final Token rightBracket;
    
    public Dimension(Token leftBracket, Exp exp, Token rightBracket) {
        this.leftBracket = leftBracket;
        this.exp = exp;
        this.rightBracket = rightBracket;
    }
    
    public Dimension(Token leftBracket, Token rightBracket) {
        this.leftBracket = leftBracket;
        this.exp = null;
        this.rightBracket = rightBracket;
    }
    
    public LinkedList<Ident> getNames() {
        if (exp != null) {
            return exp.getNames();
        }
        return new LinkedList<>();
    }
    
    public int getMaxLine() {
        if (rightBracket != null) {
            return rightBracket.getLine();
        } else if (exp != null) {
            return exp.getMaxLine();
        } else {
            return leftBracket.getLine();
        }
    }
    
    public boolean hasRightBracket() {
        return rightBracket != null;
    }
    
    public void analyse(AnalysisState state) {
        if (exp != null) {
            exp.analyse(state);
        }
        if (rightBracket == null) {
            if (exp == null) {
                state.addError(new Error(leftBracket.getLine(), ErrorType.LACK_R_BRACKET));
            } else {
                state.addError(new Error(exp.getMaxLine(), ErrorType.LACK_R_BRACKET));
            }
        }
    }
    
    @Override
    public String toString() {
        if (exp == null) {
            return leftBracket.toString() + rightBracket;
        }
        return leftBracket.toString() + exp + rightBracket;
    }
}
