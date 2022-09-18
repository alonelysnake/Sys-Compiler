package syntax.exp.unary;

import lexer.token.Ident;
import lexer.token.Token;

public class FuncCall implements ExpUnit {
    /**
     * 函数调用
     * Ident + '(' + {Paras} + ')'
     */
    
    private Ident name;
    private Token leftParent;
    private FuncRParas paras;
    private Token rightParent;
    
    public FuncCall(Ident name, Token leftParent, FuncRParas paras, Token rightParent) {
        this.name = name;
        this.leftParent = leftParent;
        this.paras = paras;
        this.rightParent = rightParent;
    }
    
    public FuncCall(Ident name, Token leftParent, Token rightParent) {
        this.name = name;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (leftParent != null) {
            sb.append(leftParent);
        }
        if (paras != null) {
            sb.append(paras);
        }
        if (rightParent != null) {
            sb.append(rightParent);
        }
        return sb.toString();
    }
}
