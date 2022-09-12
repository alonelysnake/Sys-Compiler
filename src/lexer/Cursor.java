package lexer;

public class Cursor {
    private char cur;//当前待处理字符
    private int pos;//当前待处理字符的下一个字符的index
    private String input;
    private int len;
    
    public Cursor(String input) {
        this.input = input;
        this.pos = 0;
        this.len = input.length();
        this.next();
    }
    
    public char getCur() {
        return this.cur;
    }
    
    //当前待处理字符后面是否还有字符
    public boolean hasNext() {
        return pos < len;
    }
    
    public boolean isEOF() {
        return pos > len;
    }
    
    public char getNext() {
        return input.charAt(pos);
    }
    
    public void next() {
        if (pos < len) {
            this.cur = input.charAt(pos);
        }
        pos++;
    }
}
