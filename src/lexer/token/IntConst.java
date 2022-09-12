package lexer.token;

public class IntConst extends Token {
    /**
     * 整数常量
     */
    
    private final int val;
    
    public IntConst(String name, int line) {
        super(TokenCategory.INTCONST, name, line);
        this.val = Integer.parseInt(name);
    }
    
    public int getVal() {
        return val;
    }
}
