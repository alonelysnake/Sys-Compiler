package lexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TokenCategory {
    IDENT("IDENFR"),
    INTCONST("INTCON"),
    FORMATSTRING("STRCON"),
    MAIN("MAINTK", "main"),
    CONST("CONSTTK", "const"),
    INT("INTTK", "int"),
    BREAK("BREAKTK", "break"),
    CONTINUE("CONTINUETK", "continue"),
    IF("IFTK", "if"),
    ELSE("ELSETK", "else"),
    NOT("NOT", "!"),
    AND("AND", "&&"),
    OR("OR", "||"),
    WHILE("WHILETK", "while"),
    GETINT("GETINTTK", "getint"),
    PRINT("PRINTFTK", "printf"),
    RETURN("RETURNTK", "return"),
    PLUS("PLUS", "+"),
    MINUS("MINU", "-"),
    VOID("VOIDTK", "void"),
    MULT("MULT", "*"),
    DIV("DIV", "/"),
    MOD("MOD", "%"),
    LT("LSS", "<"),
    LE("LEQ", "<="),
    GT("GRE", ">"),
    GE("GEQ", ">="),
    EQ("EQL", "=="),
    NEQ("NEQ", "!="),
    ASSIGN("ASSIGN", "="),
    SEMICN("SEMICN", ";"),
    COMMA("COMMA", ","),
    L_PARENT("LPARENT", "("),
    R_PARENT("RPARENT", ")"),
    L_BRACK("LBRACK", "["),
    R_BRACK("RBRACK", "]"),
    L_BRACE("LBRACE", "{"),
    R_BRACE("RBRACE", "}"),
    
    COMMENT("");
    
    private final String categoryCode;//类别码
    private final String realCode;//源代码中的实际字符
    
    TokenCategory(String categoryCode, String realCode) {
        this.categoryCode = categoryCode;
        this.realCode = realCode;
    }
    
    TokenCategory(String categoryCode) {
        this.categoryCode = categoryCode;
        this.realCode = null;//ident constint format不存在此值
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public String getRealCode() {
        return realCode;
    }
    
    public static final Map<String, TokenCategory> KEYWORD =
            Collections.unmodifiableMap(new HashMap<String, TokenCategory>() {
                {
                    this.put("main", TokenCategory.MAIN);
                    this.put("const", TokenCategory.CONST);
                    this.put("int", TokenCategory.INT);
                    this.put("break", TokenCategory.BREAK);
                    this.put("continue", TokenCategory.CONTINUE);
                    this.put("if", TokenCategory.IF);
                    this.put("else", TokenCategory.ELSE);
                    this.put("while", TokenCategory.WHILE);
                    this.put("getint", TokenCategory.GETINT);
                    this.put("printf", TokenCategory.PRINT);
                    this.put("return", TokenCategory.RETURN);
                    this.put("void", TokenCategory.VOID);
                }
            });
}
