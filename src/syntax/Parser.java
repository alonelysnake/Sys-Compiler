package syntax;

import lexer.token.Token;
import lexer.token.TokenCategory;

import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
    private final ListIterator<Token> tokenIterator;
    
    public Parser(LinkedList<Token> tokens) {
        //iterator全部为浅拷贝，可以实现共同步进，不再需要一个global的变量
        this.tokenIterator = tokens.listIterator();
    }
    
    public Parser(ListIterator<Token> tokenIterator) {
        this.tokenIterator = tokenIterator;
    }
    
    public ListIterator<Token> getTokenIterator() {
        return tokenIterator;
    }
    
    public void back2Point(int index) {
        while (tokenIterator.nextIndex() != index) {
            tokenIterator.previous();
        }
    }
    
    protected Token getNext() {
        if (hasNext()) {
            return tokenIterator.next();
        }
        return null;
    }
    
    protected Token getSpecialToken(TokenCategory type) {
        Token token;
        if (hasNext()) {
            token = getNext();
            if (token.getType().equals(type)) {
                return token;
            }
            previous();
        }
        return null;
    }
    
    protected boolean hasNext() {
        return tokenIterator.hasNext();
    }
    
    protected void previous() {
        tokenIterator.previous();
    }
}
