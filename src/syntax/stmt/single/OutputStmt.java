package syntax.stmt.single;

import lexer.token.FormatString;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.Iterator;
import java.util.LinkedList;

public class OutputStmt extends SingleStmt {
    private final Token printf;
    private final Token leftParent;
    private final FormatString str;
    private final LinkedList<Token> commas;
    private final LinkedList<Exp> paras;
    private final Token rightParent;
    
    public OutputStmt(Token printf, Token leftParent, FormatString str,
                      LinkedList<Token> commas, LinkedList<Exp> paras,
                      Token rightParent, Token semicolon) {
        super(semicolon);
        this.printf = printf;
        this.leftParent = leftParent;
        this.str = str;
        this.commas = commas;
        this.paras = paras;
        this.rightParent = rightParent;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printf);
        sb.append(leftParent);
        sb.append(str);
        Iterator<Token> commaIterator = commas.iterator();
        Iterator<Exp> paraIterator = paras.iterator();
        while (commaIterator.hasNext() && paraIterator.hasNext()) {
            sb.append(commaIterator.next());
            sb.append(paraIterator.next());
        }
        sb.append(rightParent);
        sb.append(getSemicolon());
        return sb.toString();
    }
}
