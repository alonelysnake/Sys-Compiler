package syntax.decl;

import error.AnalysisState;
import lexer.token.Token;

import java.util.Iterator;
import java.util.LinkedList;

public class ArrInit implements InitVal {
    private final boolean constFlag;
    private final Token leftBrace;
    private final LinkedList<InitVal> vals;
    private final LinkedList<Token> commas;
    private final Token rightBrace;
    
    public ArrInit(boolean constFlag, Token leftBrace, LinkedList<InitVal> vals,
                   LinkedList<Token> commas, Token rightBrace) {
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
    public int getMaxLine() {
        return rightBrace.getLine();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        if (vals != null) {
            vals.forEach(val -> val.analyse(state));
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBrace);
        
        if (vals != null) {
            Iterator<InitVal> valIterator = vals.iterator();
            sb.append(valIterator.next());
            assert commas != null;
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
