package syntax.stmt.multi;

import lexer.token.Token;
import syntax.BlockItem;

import java.util.LinkedList;

public class Block implements MultiStmt {
    private Token leftBrace;
    private LinkedList<BlockItem> items;
    private Token rightBrace;
    
    public Block(Token leftBrace, LinkedList<BlockItem> items, Token rightBrace) {
        this.leftBrace = leftBrace;
        this.items = items;
        this.rightBrace = rightBrace;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBrace);
        for (BlockItem item : items) {
            sb.append(item);
        }
        sb.append(rightBrace);
        
        sb.append("<Block>\n");
        return sb.toString();
    }
}
