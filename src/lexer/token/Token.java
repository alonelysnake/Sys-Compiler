package lexer.token;

public class Token {
    private final TokenCategory type;
    private final String name;
    private final int line;
    
    //常量、变量、格式化字符串的初始化
    public Token(TokenCategory type, String name, int line) {
        this.type = type;
        this.name = name;
        this.line = line;
    }
    
    public TokenCategory getType() {
        return type;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        return type.getCategoryCode() + " " + name + "\n";
    }
}
