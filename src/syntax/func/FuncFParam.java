package syntax.func;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.unary.Dimension;

import java.util.LinkedList;

public class FuncFParam {
    private Token type;//固定为int
    private Ident name;
    
    private Dimension firstDimension;
    private LinkedList<Dimension> followDimensions;
    
    public FuncFParam(Token type, Ident name) {
        this.type = type;
        this.name = name;
        this.firstDimension = null;
        this.followDimensions = null;
    }
    
    public FuncFParam(Token type, Ident name, Dimension firstDimension) {
        this.type = type;
        this.name = name;
        this.firstDimension = firstDimension;
        this.followDimensions = null;
    }
    
    public FuncFParam(Token type, Ident name, Dimension firstDimension, LinkedList<Dimension> followDimensions) {
        this.type = type;
        this.name = name;
        this.firstDimension = firstDimension;
        this.followDimensions = followDimensions;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(name);
        if (this.firstDimension != null) {
            sb.append(firstDimension);
            if (this.followDimensions != null) {
                this.followDimensions.forEach(sb::append);
            }
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
