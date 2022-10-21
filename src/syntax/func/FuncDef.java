package syntax.func;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.FuncEntry;
import middle.instruction.INode;
import middle.instruction.Return;
import syntax.SyntaxNode;
import syntax.stmt.multi.Block;

public class FuncDef implements SyntaxNode {
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
    
    public int getParaNum() {
        if (params == null) {
            return 0;
        }
        return params.paraNum();
    }
    
    public int paraNum() {
        if (params == null) {
            return 0;
        }
        return params.paraNum();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //声明进入自定义函数处理
        state.setCurFunc(this);
        //函数名重定义
        if (!state.addFunc(this)) {
            state.addError(new Error(name.getLine(), ErrorType.REDEFINED_IDENT));
        }
        // 栈顶放入符号表并处理形参
        state.funcCreateSymTable();
        
        if (params != null) {
            params.analyse(state);
        }
        // 处理右括号
        if (this.rightParent == null) {
            if (params == null) {
                state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));
            } else {
                state.addError(new Error(params.getMaxLine(), ErrorType.LACK_R_PARENT));
            }
        }
        // 处理block
        content.analyse(state);
        //完成自定义函数处理
        state.setCurFunc(null);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        INode first = new FuncEntry(name.getName(), getParaNum());
        state.getLabelTable().connect(name.getName(), first);
        INode last = first;
        state.inBlock();
        if (params != null) {
            BlockInfo paramsNode = params.generateIcode(state);
            last = last.insert(paramsNode.getFirst());
        }
        BlockInfo body = content.generateIcode(state);
        last = last.insert(body.getFirst());
        // 返回int的保证最后有return
        if (returnType.getType().getType().equals(TokenCategory.VOID)) {
            last = last.insert(new Return());
        }
        return new BlockInfo(null, first, last);
    }
    
    public TokenCategory getType() {
        return returnType.getType().getType();
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
        if (rightParent != null) {
            sb.append(rightParent);
        }
        sb.append(content);
        sb.append("<FuncDef>\n");
        return sb.toString();
    }
}
