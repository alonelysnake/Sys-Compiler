package syntax.stmt.single;

import lexer.token.Token;
import syntax.exp.unary.LVal;

public class InputStmt extends SingleStmt {
    private LVal lVal;
    private Token assign;
    private Token name;
    private Token leftParent;
    private Token rightParent;
    
    public InputStmt(LVal lVal, Token assign, Token name, Token leftParent, Token rightParent, Token semicolon) {
        super(semicolon);
        this.lVal = lVal;
        this.assign = assign;
        this.name = name;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
    }
    
    @Override
    public String toString() {
        return lVal.toString() +
                assign +
                name +
                leftParent +
                rightParent +
                getSemicolon();
    }
}
