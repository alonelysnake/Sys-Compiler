package lexer;

public class Token {
    private TokenCategory type;
    private String name;
    
    //常量、变量、格式化字符串的初始化
    public Token(TokenCategory type, String name) {
        this.type = type;
        this.name = name;
    }
    
    @Override
    public String toString() {
        //TODO 不同种类的符号不同输出
        return type.getCategoryCode() + " " + name;
        //return TokenCategory.TOKEN2CODE.get(this.type) + " " + name;
    }
}
