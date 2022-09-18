package syntax.exp.unary;

import lexer.token.Token;
import syntax.exp.multi.Exp;

public class Dimension {
    /**
     * 数组维度
     * '[' + exp/constexp + ']'
     */
    private Token leftBracket;
    private Exp exp;
    private Token rightBracket;
    
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
    
    @Override
    public String toString() {
        if (exp == null) {
            return leftBracket.toString() + rightBracket;
        }
        return leftBracket.toString() + exp + rightBracket;
    }
}
