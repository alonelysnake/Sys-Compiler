package syntax.exp.unary;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncRParas {
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
