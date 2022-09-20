package lexer.token;

public class FormatString extends Token {
    /**
     * 格式化字符串
     */
    
    private final String content;//不包含起始和终止的'"'的字符串（即{<Char>}）
    
    public FormatString(String name, int line) {
        super(TokenCategory.FORMATSTRING, name, line);
        content = name.substring(1, name.length() - 1);
    }
    
    public String getContent() {
        return content;
    }
}
