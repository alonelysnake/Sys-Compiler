package lexer.token;

public class FormatString extends Token {
    /**
     * 格式化字符串
     */
    
    private final String content;
    
    public FormatString(String name, int line) {
        super(TokenCategory.FORMATSTRING, name, line);
        content = name.substring(1, name.length() - 1);
    }
    
    public String getContent() {
        return content;
    }
}
