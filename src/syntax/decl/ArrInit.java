package syntax.decl;

import lexer.token.Token;

import java.util.Iterator;
import java.util.LinkedList;

public class ArrInit implements InitVal {
    private boolean constFlag;
    private Token leftBrace;
    private LinkedList<InitVal> vals;
    private LinkedList<Token> commas;
    private Token rightBrace;
    
    public ArrInit(boolean constFlag, Token leftBrace, LinkedList<InitVal> vals, LinkedList<Token> commas, Token rightBrace) {
        this.constFlag = constFlag;
        this.leftBrace = leftBrace;
        this.vals = vals;
        this.commas = commas;
        this.rightBrace = rightBrace;
    }
    
    public ArrInit(boolean constFlag, Token leftBrace, Token rightBrace) {
        this.constFlag = constFlag;
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.vals = null;
        this.commas = null;
    }
    
    @Override
    public boolean isConst() {
        return constFlag;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBrace);
        
        if (vals != null) {
            Iterator<InitVal> valIterator = vals.iterator();
            sb.append(valIterator.next());
            Iterator<Token> commaIterator = commas.iterator();
            while (valIterator.hasNext()) {
                sb.append(commaIterator.next());
                sb.append(valIterator.next());
            }
        }
        if (rightBrace != null) {
            sb.append(rightBrace);
        }
        if (isConst()) {
            sb.append("<ConstInitVal>\n");
        } else {
            sb.append("<InitVal>\n");
        }
        return sb.toString();
    }
}
