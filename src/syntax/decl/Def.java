package syntax.decl;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.unary.Dimension;

import java.util.LinkedList;

public class Def {
    private Ident name;
    private LinkedList<Dimension> dimensions;
    
    private Token assign;
    private InitVal val;
    
    private boolean constFlag;
    
    //无初值的定义
    public Def(Ident name, LinkedList<Dimension> dimensions) {
        this.name = name;
        this.dimensions = dimensions;
        this.assign = null;
        this.val = null;
        this.constFlag = false;
    }
    
    //有初值的定义
    public Def(Ident name, LinkedList<Dimension> dimensions, Token assign, InitVal val, boolean constFlag) {
        this.name = name;
        this.dimensions = dimensions;
        this.assign = assign;
        this.val = val;
        this.constFlag = constFlag;
    }
    
    public boolean isInit() {
        return this.val != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        dimensions.forEach(sb::append);
        if (isInit()) {
            sb.append(assign);
            sb.append(val);
        }
        if (constFlag) {
            sb.append("<ConstDef>\n");
        } else {
            sb.append("<VarDef>\n");
        }
        return sb.toString();
    }
}
