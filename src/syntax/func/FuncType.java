package syntax.func;

import lexer.token.Token;

public class FuncType {
    private final Token type;
    
    public FuncType(Token type) {
        this.type = type;
    }
    
    public Token getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return type + "<FuncType>\n";
    }
}
