package syntax.decl;

import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.Parser;
import syntax.exp.ExpParser;
import syntax.exp.multi.ConstExp;
import syntax.exp.unary.Dimension;

import java.util.LinkedList;
import java.util.ListIterator;

public class DeclParser extends Parser {
    public DeclParser(ListIterator<Token> tokenIterator) {
        super(tokenIterator);
    }
    
    public Decl parseConstDecl(Token constSym, Token intSym) {
        //ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        //TODO 是否需要确认合法性
        LinkedList<Token> commas = new LinkedList<>();
        LinkedList<Def> defs = new LinkedList<>();
        Token semicolon = null;
        defs.addLast(parseDef(true, (Ident) getNext()));
        while (hasNext()) {
            Token first = getNext();
            if (first.getType().equals(TokenCategory.COMMA)) {
                commas.addLast(first);
                first = getNext();
                if (first.getType().equals(TokenCategory.IDENT)) {
                    defs.addLast(parseDef(true, (Ident) first));
                } else {
                    System.err.println("一行多声明时，','后没有变量，读到词：" + first);
                }
            } else if (first.getType().equals(TokenCategory.SEMICN)) {
                semicolon = first;
                break;
            } else {
                //TODO 报错
                System.err.println("constDecl读到其他字符：" + first);
            }
        }
        return new Decl(constSym, intSym, commas, defs, semicolon);
    }
    
    public Decl parseVarDecl(Token intSym, Ident name) {
        LinkedList<Def> defs = new LinkedList<>();
        LinkedList<Token> commas = new LinkedList<>();
        Token semicolon = null;
        defs.addLast(parseDef(false, name));
        while (hasNext()) {
            Token first = getNext();
            if (first.getType().equals(TokenCategory.COMMA)) {
                commas.addLast(first);
                first = getNext();
                if (first.getType().equals(TokenCategory.IDENT)) {
                    defs.addLast(parseDef(false, (Ident) first));
                } else {
                    System.err.println("一行多声明时，','后没有变量，读到词：" + first);
                }
            } else if (first.getType().equals(TokenCategory.SEMICN)) {
                semicolon = first;
                break;
            } else {
                //TODO 报错
                System.err.println("行：" + first.getLine() + "varDecl读到其他字符：" + first);
            }
        }
        return new Decl(intSym, commas, defs, semicolon);
    }
    
    public Def parseDef(boolean constFlag, Ident name) {
        //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
        //ConstDef → Ident { Dimension } '=' ConstInitVal
        
        Token first = getNext();
        //first = '[' | '='
        LinkedList<Dimension> dimensions = new LinkedList<>();
        while (hasNext()) {
            if (first.getType().equals(TokenCategory.L_BRACK)) {
                dimensions.addLast(parseConstDimention(first));
            } else {
                break;
            }
            first = getNext();
        }
        
        //first = '=' | ';'
        if (first.getType().equals(TokenCategory.ASSIGN)) {
            InitVal val = parseVal(constFlag);
            return new Def(name, dimensions, first, val, constFlag);//保证样例不出现未定义常数的情况?
        } else {
            previous();
            return new Def(name, dimensions);
        }
    }
    
    public Dimension parseConstDimention(Token leftBrack) {
        ConstExp exp = new ExpParser(getTokenIterator()).parseConstExp();
        Token rightBrack = getNext();//TODO 无右括号报错
        
        return new Dimension(leftBrack, exp, rightBrack);
    }
    
    public InitVal parseVal(boolean constFlag) {
        //InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
        Token first = getNext();
        if (first.getType().equals(TokenCategory.L_BRACE)) {
            //first = '{'
            Token leftBrace = first;
            first = getNext();
            if (first.getType().equals(TokenCategory.R_BRACE)) {
                //空的初始化
                return new ArrInit(constFlag, leftBrace, first);
            } else {
                previous();
                LinkedList<InitVal> vals = new LinkedList<>();
                LinkedList<Token> commas = new LinkedList<>();
                vals.addLast(parseVal(constFlag));
                while (hasNext()) {
                    first = getNext();
                    //first = ',' | '}'
                    if (first.getType().equals(TokenCategory.COMMA)) {
                        commas.addLast(first);
                        vals.addLast(parseVal(constFlag));
                    } else if (first.getType().equals(TokenCategory.R_BRACE)) {
                        //first = '}'
                        break;
                    } else {
                        System.err.println("数组定义的初值的右大括号错误，读到词：" + first);//TODO 报错
                        return null;
                    }
                }
                return new ArrInit(constFlag, leftBrace, vals, commas, first);
            }
        } else {
            //first = exp
            previous();
            if (constFlag) {
                return new ExpInit(true, new ExpParser(getTokenIterator()).parseConstExp());
            } else {
                return new ExpInit(false, new ExpParser(getTokenIterator()).parseExp());
            }
        }
    }
}
