package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.decl.BType;
import syntax.exp.multi.Exp;
import syntax.func.FuncDef;
import syntax.func.FuncFParam;

import java.util.Iterator;
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
    
    public int getParaNum() {
        if (paras == null) {
            return 0;
        }
        return paras.paraNum();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //检查函数名是否存在
        if (!state.containsFunc(name.getName())) {
            state.addError(new Error(name.getLine(), ErrorType.UNDEFINED_IDENT));
        }
        //检查参数个数是否一致
        FuncDef func = state.getFunc(name.getName());
        if (func.getParaNum() != this.getParaNum()) {
            state.addError(new Error(name.getLine(), ErrorType.MISMATCH_PARA_NUM));
        }
        //检查参数类型是否一致
        //表达式运算规则：对于非单项的表达式，运算符两边的表达式必须有相同维度且不为void，此时返回值为int，否则应报错
        //只有不涉及任何表达式运算时（即整个exp为一个左值lval或常量number或funccall）才根据情况确定其维度，否则一律为item（int）
        if (paras != null && func.getParams() != null) {
            Iterator<Exp> callIterator = paras.getParas().iterator();
            Iterator<FuncFParam> defIterator = func.getParams().getParas().iterator();
            while (callIterator.hasNext() && defIterator.hasNext()) {
                //TODO 此处如果是未定义函数该如何处理?
                BType callType = callIterator.next().getExpType(state);
                BType defType = defIterator.next().getType();
                if (callType == null || callType != defType) {
                    state.addError(new Error(name.getLine(), ErrorType.MISMATCH_PARA_TYPE));
                }
            }
        }
        
        
        //检查右括号
        if (rightParent == null) {
            if (paras == null) {
                state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));
            } else {
                state.addError(new Error(paras.getMaxLine(), ErrorType.LACK_R_PARENT));
            }
        }
    }
    
    public String getFuncName() {
        return name.getName();
    }
    
    @Override
    public int getMaxLine() {
        if (rightParent != null) {
            return rightParent.getLine();
        } else if (paras != null) {
            return paras.getMaxLine();
        } else {
            return leftParent.getLine();
        }
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        if (paras == null) {
            return new LinkedList<>();
        }
        return paras.getNames();
    }
    
    public int paraNum() {
        if (paras == null) {
            return 0;
        } else {
            return paras.paraNum();
        }
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
