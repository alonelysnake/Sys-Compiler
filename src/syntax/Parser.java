package syntax;

import lexer.token.Token;

import java.util.LinkedList;
import java.util.ListIterator;

public class Parser {
    private ListIterator<Token> tokenIterator;
    
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
    
    protected Token getNext() {
        if (hasNext()) {
            return tokenIterator.next();
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
