package syntax.func;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.stmt.multi.Block;

public class FuncDef {
    private final FuncType returnType;
    private final Ident name;
    private final Token leftParent;
    private final FuncFParams params;
    private final Token rightParent;
    private final Block content;
    
    //有参函数
    public FuncDef(Token type, Ident name, Token leftParent, FuncFParams params, Token rightParent,
                   Block content) {
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
    
    //统计自定义函数名
    public Ident getName() {
        return name;
    }
    
    //统计形参
    public FuncFParams getParams() {
        return params;
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
