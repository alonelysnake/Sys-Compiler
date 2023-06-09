package syntax.func;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.Nop;
import syntax.SyntaxNode;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncFParams implements SyntaxNode {
    private final LinkedList<Token> commas;
    private final LinkedList<FuncFParam> paras;
    
    public FuncFParams(LinkedList<Token> commas, LinkedList<FuncFParam> paras) {
        this.commas = commas;
        this.paras = paras;
    }
    
    //返回函数形参中定义的变量，用于建立符号表与错误处理
    public LinkedList<Ident> getParaNames() {
        LinkedList<Ident> names = new LinkedList<>();
        paras.forEach((para -> names.addLast(para.getName())));
        return names;
    }
    
    public int getMaxLine() {
        return paras.getLast().getMaxLine();
    }
    
    public LinkedList<FuncFParam> getParas() {
        return paras;
    }
    
    public int paraNum() {
        return paras.size();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //此时已经进入自定义函数体的栈符号表中
        paras.forEach(para -> para.analyse(state));
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        INode first = new Nop();
        INode last = first;
        for (FuncFParam para : paras) {
            last = last.insert(para.generateIcode(state).getFirst());
        }
        return new BlockInfo(null, first, last);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<FuncFParam> paraIterator = paras.iterator();
        sb.append(paraIterator.next());
        Iterator<Token> commaIterator = commas.iterator();
        while (paraIterator.hasNext()) {
            sb.append(commaIterator.next());
            sb.append(paraIterator.next());
        }
        sb.append("<FuncFParams>\n");
        return sb.toString();
    }
}
