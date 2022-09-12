package lexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TokenCategory {
    IDENT,
    INTCONST,
    FORMATSTRING,
    MAIN,
    CONST,
    INT,
    BREAK,
    CONTINUE,
    IF,
    ELSE,
    NOT,
    AND,
    OR,
    WHILE,
    GETINT,
    PRINT,
    RETURN,
    PLUS,
    MINUS,
    VOID,
    MULT,
    DIV,
    MOD,
    LT,
    LE,
    GT,
    GE,
    EQ,
    NEQ,
    ASSIGN,
    SEMICN,
    COMMA,
    L_PARENT,
    R_PARENT,
    L_BRACK,
    R_BRACK,
    L_BRACE,
    R_BRACE,
    
    COMMENT;
    
    public static final Map<TokenCategory, String> TOKEN2STR =
            Collections.unmodifiableMap(new HashMap<TokenCategory, String>() {
                {
                    this.put(TokenCategory.MAIN, "main");
                    this.put(TokenCategory.CONST, "const");
                    this.put(TokenCategory.INT, "int");
                    this.put(TokenCategory.BREAK, "break");
                    this.put(TokenCategory.CONTINUE, "continue");
                    this.put(TokenCategory.IF, "if");
                    this.put(TokenCategory.ELSE, "else");
                    this.put(TokenCategory.NOT, "!");
                    this.put(TokenCategory.AND, "&&");
                    this.put(TokenCategory.OR, "||");
                    this.put(TokenCategory.WHILE, "while");
                    this.put(TokenCategory.GETINT, "getint");
                    this.put(TokenCategory.PRINT, "printf");
                    this.put(TokenCategory.RETURN, "return");
                    this.put(TokenCategory.PLUS, "+");
                    this.put(TokenCategory.MINUS, "-");
                    this.put(TokenCategory.VOID, "void");
                    this.put(TokenCategory.MULT, "*");
                    this.put(TokenCategory.DIV, "/");
                    this.put(TokenCategory.MOD, "%");
                    this.put(TokenCategory.LT, "<");
                    this.put(TokenCategory.LE, "<=");
                    this.put(TokenCategory.GT, ">");
                    this.put(TokenCategory.GE, ">=");
                    this.put(TokenCategory.EQ, "==");
                    this.put(TokenCategory.NEQ, "!=");
                    this.put(TokenCategory.ASSIGN, "=");
                    this.put(TokenCategory.SEMICN, ";");
                    this.put(TokenCategory.COMMA, ",");
                    this.put(TokenCategory.L_PARENT, "(");
                    this.put(TokenCategory.R_PARENT, ")");
                    this.put(TokenCategory.L_BRACK, "[");
                    this.put(TokenCategory.R_BRACK, "]");
                    this.put(TokenCategory.L_BRACE, "{");
                    this.put(TokenCategory.R_BRACE, "}");
                }
            });
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
    public static final Map<TokenCategory, String> TOKEN2CODE =
            Collections.unmodifiableMap(new HashMap<TokenCategory, String>() {
                {
                    this.put(TokenCategory.IDENT, "IDENFR");
                    this.put(TokenCategory.INTCONST, "INTCON");
                    this.put(TokenCategory.FORMATSTRING, "STRCON");
                    this.put(TokenCategory.MAIN, "MAINTK");
                    this.put(TokenCategory.CONST, "CONSTTK");
                    this.put(TokenCategory.INT, "INTTK");
                    this.put(TokenCategory.BREAK, "BREAKTK");
                    this.put(TokenCategory.CONTINUE, "CONTINUETK");
                    this.put(TokenCategory.IF, "IFTK");
                    this.put(TokenCategory.ELSE, "ELSETK");
                    this.put(TokenCategory.NOT, "NOT");
                    this.put(TokenCategory.AND, "AND");
                    this.put(TokenCategory.OR, "OR");
                    this.put(TokenCategory.WHILE, "WHILETK");
                    this.put(TokenCategory.GETINT, "GETINTTK");
                    this.put(TokenCategory.PRINT, "PRINTFTK");
                    this.put(TokenCategory.RETURN, "RETURNTK");
                    this.put(TokenCategory.PLUS, "PLUS");
                    this.put(TokenCategory.MINUS, "MINU");
                    this.put(TokenCategory.VOID, "VOIDTK");
                    this.put(TokenCategory.MULT, "MULT");
                    this.put(TokenCategory.DIV, "DIV");
                    this.put(TokenCategory.MOD, "MOD");
                    this.put(TokenCategory.LT, "LSS");
                    this.put(TokenCategory.LE, "LEQ");
                    this.put(TokenCategory.GE, "GEQ");
                    this.put(TokenCategory.GT, "GRE");
                    this.put(TokenCategory.EQ, "EQL");
                    this.put(TokenCategory.NEQ, "NEQ");
                    this.put(TokenCategory.ASSIGN, "ASSIGN");
                    this.put(TokenCategory.SEMICN, "SEMICN");
                    this.put(TokenCategory.COMMA, "COMMA");
                    this.put(TokenCategory.L_PARENT, "LPARENT");
                    this.put(TokenCategory.R_PARENT, "RPARENT");
                    this.put(TokenCategory.L_BRACK, "LBRACK");
                    this.put(TokenCategory.R_BRACK, "RBRACK");
                    this.put(TokenCategory.L_BRACE, "LBRACE");
                    this.put(TokenCategory.R_BRACE, "RBRACE");
                }
            });
}
