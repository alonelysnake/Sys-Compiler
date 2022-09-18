package syntax.exp.unary;

import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncRParas {
    /**
     * exp1 + ',' + exp2 + ...
     */
    
    private LinkedList<Exp> paras;
    private LinkedList<Token> commas;
    
    public FuncRParas(LinkedList<Exp> paras, LinkedList<Token> commas) {
        this.paras = paras;
        this.commas = commas;
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
