package syntax.func;

import lexer.token.Token;

import java.util.Iterator;
import java.util.LinkedList;

public class FuncFParams {
    private final LinkedList<Token> commas;
    private final LinkedList<FuncFParam> paras;
    
    public FuncFParams(LinkedList<Token> commas, LinkedList<FuncFParam> paras) {
        this.commas = commas;
        this.paras = paras;
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
