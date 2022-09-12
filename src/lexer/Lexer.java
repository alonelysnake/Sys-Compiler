package lexer;

import lexer.token.FormatString;
import lexer.token.Ident;
import lexer.token.IntConst;
import lexer.token.Token;
import lexer.token.TokenCategory;

import java.util.ArrayList;

public class Lexer {
    private Cursor cursor;
    private String curStr;//ident或者保留字或数字
    
    public Lexer(String input) {
        this.cursor = new Cursor(input);
    }
    
    //跳过空白符
    private void skipBlank() {
        while (!cursor.isEOF() && Character.isWhitespace(cursor.getCur())) {
            cursor.next();
        }
    }
    
    //跳过单行注释
    private void skipLine() {
        while (!cursor.isEOF() && cursor.getCur() != '\n') {
            cursor.next();
        }
        cursor.next();//skip '\n'
    }
    
    //跳过多行注释
    private void skipBlock() {
        cursor.next();//skip '/'
        cursor.next();//skip '*'
        while (cursor.hasNext() && (cursor.getCur() != '*' || cursor.getNext() != '/')) {
            cursor.next();
        }
        cursor.next();//skip '*'
        cursor.next();//skip '/'
    }
    
    //检测格式化字符串
    private TokenCategory cutFormat(StringBuilder sb) {
        boolean flag = false;//当前是否被'\'转义，仅考虑'\n'的情况，其他情况是否认为错误？
        while (!this.cursor.isEOF()) {
            char c = cursor.getCur();
            sb.append(c);
            cursor.next();
            if (c == '"') {
                this.curStr = sb.toString();
                return TokenCategory.FORMATSTRING;
            }
        }
        return null;//报错
    }
    
    private TokenCategory cutNumber(StringBuilder sb) {
        while (!this.cursor.isEOF()) {
            char c = cursor.getCur();
            if (Character.isDigit(c)) {
                sb.append(c);
                cursor.next();
            } else {
                break;
            }
        }
        this.curStr = sb.toString();
        return TokenCategory.INTCONST;
    }
    
    private TokenCategory cutIdent(StringBuilder sb) {
        while (!this.cursor.isEOF()) {
            char c = cursor.getCur();
            if (Character.isDigit(c) || Character.isLetter(c) || c == '_') {
                sb.append(c);
                cursor.next();
            } else {
                break;
            }
        }
        this.curStr = sb.toString();
        return judgeIdent(this.curStr);
    }
    
    private TokenCategory judgeIdent(String ident) {
        if (TokenCategory.KEYWORD.containsKey(ident)) {
            return TokenCategory.KEYWORD.get(ident);
        }
        return TokenCategory.IDENT;
    }
    
    private Token createToken() {
        int line = this.cursor.getLine();
        TokenCategory type = getTokenType();
        //TODO 报错返回
        if (type == null) {
            return null;
        }
        switch (type) {
            case COMMENT:
                return null;
            case INTCONST:
                return new IntConst(this.curStr, line);
            case IDENT:
                return new Ident(this.curStr, line);
            case FORMATSTRING:
                return new FormatString(this.curStr, line);
            default:
                return new Token(type, type.getRealCode(), line);
        }
    }
    
    private TokenCategory getTokenType() {
        char first = cursor.getCur();
        cursor.next();//skip first char
        switch (first) {
            case '!':
                if (!cursor.isEOF() && cursor.getCur() == '=') {
                    cursor.next();
                    return TokenCategory.NEQ;
                } else {
                    return TokenCategory.NOT;
                }
            case '&':
                if (!cursor.isEOF() && cursor.getCur() == '&') {
                    cursor.next();
                    return TokenCategory.AND;
                } else {
                    return null;//TODO 报错
                }
            case '|':
                if (!cursor.isEOF() && cursor.getCur() == '|') {
                    cursor.next();
                    return TokenCategory.OR;
                } else {
                    return null;//TODO 报错
                }
            case '+':
                return TokenCategory.PLUS;
            case '-':
                return TokenCategory.MINUS;
            case '*':
                return TokenCategory.MULT;
            case '/':
                if (!cursor.isEOF()) {
                    if (cursor.getCur() == '/') {
                        //单行注释
                        skipLine();
                        return TokenCategory.COMMENT;
                    } else if (cursor.getCur() == '*') {
                        //多行注释
                        skipBlock();
                        return TokenCategory.COMMENT;
                    }
                }
                return TokenCategory.DIV;
            case '%':
                return TokenCategory.MOD;
            case '<':
                if (!cursor.isEOF() && cursor.getCur() == '=') {
                    cursor.next();
                    return TokenCategory.LE;
                } else {
                    return TokenCategory.LT;
                }
            case '>':
                if (!cursor.isEOF() && cursor.getCur() == '=') {
                    cursor.next();
                    return TokenCategory.GE;
                } else {
                    return TokenCategory.GT;
                }
            case '=':
                if (!cursor.isEOF() && cursor.getCur() == '=') {
                    cursor.next();
                    return TokenCategory.EQ;
                } else {
                    return TokenCategory.ASSIGN;
                }
            case ';':
                return TokenCategory.SEMICN;
            case ',':
                return TokenCategory.COMMA;
            case '(':
                return TokenCategory.L_PARENT;
            case ')':
                return TokenCategory.R_PARENT;
            case '[':
                return TokenCategory.L_BRACK;
            case ']':
                return TokenCategory.R_BRACK;
            case '{':
                return TokenCategory.L_BRACE;
            case '}':
                return TokenCategory.R_BRACE;
            case '"':
                return cutFormat(new StringBuilder(String.valueOf(first)));
            default:
                if (Character.isDigit(first)) {
                    return cutNumber(new StringBuilder(String.valueOf(first)));
                } else if (Character.isLetter(first) || first == '_') {
                    return cutIdent(new StringBuilder(String.valueOf(first)));
                } else {
                    return null;//TODO 报错
                }
        }
    }
    
    public ArrayList<Token> tackle() {
        ArrayList<Token> ret = new ArrayList<>();
        while (!cursor.isEOF()) {
            skipBlank();
            if (!cursor.isEOF()) {
                Token token = createToken();
                if (token != null) {
                    //不是注释
                    ret.add(token);
                }
            }
        }
        return ret;
    }
}
