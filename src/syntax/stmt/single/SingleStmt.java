package syntax.stmt.single;

import lexer.token.Token;
import syntax.BlockItem;

public class SingleStmt implements BlockItem {
    /**
     * 所有单行语句，都一定含有semicolon
     */
    private final Token semicolon;
    
    public SingleStmt(Token semicolon) {
        this.semicolon = semicolon;
    }
    
    public Token getSemicolon() {
        return semicolon;
    }
    
    public boolean hasSemicolon() {
        return semicolon != null;
    }
    
    @Override
    public String toString() {
        //TODO 为null时
        return semicolon.toString();
    }
}
