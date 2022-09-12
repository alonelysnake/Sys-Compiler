package lexer.token;

public class Ident extends Token {
    
    private final String name;
    
    public Ident(String name, int line) {
        super(TokenCategory.IDENT, name, line);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
