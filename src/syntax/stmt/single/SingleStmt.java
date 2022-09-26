package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import lexer.token.Token;
import syntax.BlockItem;
import syntax.SyntaxNode;

public class SingleStmt implements SyntaxNode {
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
    public void analyse(AnalysisState state) {
    }
    
    @Override
    public String toString() {
        if (semicolon != null) {
            return semicolon.toString();
        } else {
            return "";
        }
    }
}
