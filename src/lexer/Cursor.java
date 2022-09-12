package lexer;

import java.util.ArrayList;

public class Cursor {
    private char cur;//当前待处理字符
    private int pos;//当前待处理字符的下一个字符的index
    private String input;
    //private ArrayList<Integer> lineIndexs;//每一行第一个字符的pos
    private int line;//当前读到的词所在行数
    private int len;
    
    public Cursor(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
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
    
    public int getLine() {
        return line;
    }
    
    public void next() {
        if (pos < len) {
            if (this.cur == '\n') {
                this.line++;
            }
            this.cur = input.charAt(pos);
        }
        pos++;
    }
}
