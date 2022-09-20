package syntax.exp.unary;

import lexer.token.Ident;
import lexer.token.Token;

import java.util.LinkedList;

public class FuncCall implements ExpUnit {
    /**
     * 函数调用
     * Ident + '(' + {Paras} + ')'
     */
    
    private final Ident name;
    private final Token leftParent;
    private final FuncRParas paras;
    private final Token rightParent;
    
    public FuncCall(Ident name, Token leftParent, FuncRParas paras, Token rightParent) {
        this.name = name;
        this.leftParent = leftParent;
        this.paras = paras;
        this.rightParent = rightParent;
    }
    
    public FuncCall(Ident name, Token leftParent, Token rightParent) {
        this.name = name;
        this.leftParent = leftParent;
        this.paras = null;
        this.rightParent = rightParent;
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        if (paras == null) {
            return new LinkedList<>();
        }
        return paras.getNames();
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
