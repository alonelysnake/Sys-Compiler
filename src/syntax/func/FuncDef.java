package syntax.func;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.stmt.multi.Block;

public class FuncDef {
    private FuncType returnType;
    private Ident name;
    private Token leftParent;
    private FuncFParams params;
    private Token rightParent;
    private Block content;
    
    //有参函数
    public FuncDef(Token type, Ident name, Token leftParent, FuncFParams params, Token rightParent, Block content) {
        this.returnType = new FuncType(type);
        this.name = name;
        this.leftParent = leftParent;
        this.params = params;
        this.rightParent = rightParent;
        this.content = content;
    }
    
    //无参函数
    public FuncDef(Token type, Ident name, Token leftParent, Token rightParent, Block content) {
        this.returnType = new FuncType(type);
        this.name = name;
        this.leftParent = leftParent;
        this.params = null;
        this.rightParent = rightParent;
        this.content = content;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType);
        sb.append(name);
        sb.append(leftParent);
        if (params != null) {
            sb.append(params);
        }
        //TODO 无右括号报错
        sb.append(rightParent);
        sb.append(content);
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
