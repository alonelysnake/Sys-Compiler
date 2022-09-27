package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import syntax.SyntaxNode;
import syntax.exp.multi.Exp;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncRParas implements SyntaxNode {
    /**
     * exp1 + ',' + exp2 + ...
     */
    
    private final LinkedList<Exp> paras;
    private final LinkedList<Token> commas;
    
    public FuncRParas(LinkedList<Exp> paras, LinkedList<Token> commas) {
        this.paras = paras;
        this.commas = commas;
    }
    
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        paras.forEach(para -> names.addAll(para.getNames()));
        return names;
    }
    
    public LinkedList<Exp> getParas() {
        return paras;
    }
    
    public int getMaxLine() {
        return paras.getLast().getMaxLine();
    }
    
    public int paraNum() {
        return paras.size();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //检查各个参数格式是否正确
        for (Exp exp : paras) {
            exp.analyse(state);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Exp> paraIterator = paras.iterator();
        Iterator<Token> commaIterator = commas.iterator();
        sb.append(paraIterator.next());
        while (paraIterator.hasNext()) {
            sb.append(commaIterator.next());
            sb.append(paraIterator.next());
        }
        return sb.append("<FuncRParams>\n").toString();
    }
}
