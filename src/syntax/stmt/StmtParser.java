package syntax.stmt;

import lexer.token.FormatString;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.BlockItem;
import syntax.Parser;
import syntax.decl.DeclParser;
import syntax.exp.ExpParser;
import syntax.exp.multi.Cond;
import syntax.exp.multi.Exp;
import syntax.exp.unary.LVal;
import syntax.stmt.multi.Block;
import syntax.stmt.multi.IfStmt;
import syntax.stmt.multi.JudgeStmt;
import syntax.stmt.multi.WhileStmt;
import syntax.stmt.single.AssignStmt;
import syntax.stmt.single.BreakStmt;
import syntax.stmt.single.ContinueStmt;
import syntax.stmt.single.ExpStmt;
import syntax.stmt.single.InputStmt;
import syntax.stmt.single.OutputStmt;
import syntax.stmt.single.ReturnStmt;

import java.util.LinkedList;
import java.util.ListIterator;

public class StmtParser extends Parser {
    public StmtParser(ListIterator<Token> tokenIterator) {
        super(tokenIterator);
    }
    
    public Block parseBlock(Token leftBrace) {
        LinkedList<BlockItem> items = new LinkedList<>();
        Token rightBrace = null;
        Token first;
        while (hasNext()) {
            first = getNext();
            if (first.getType().equals(TokenCategory.R_BRACE)) {
                rightBrace = first;
                break;
            }
            items.addLast(parseBlockItem(first));
        }
        return new Block(leftBrace, items, rightBrace);
    }
    
    public BlockItem parseBlockItem(Token first) {
        //BlockItem → Decl | Stmt
        if (first.getType().equals(TokenCategory.CONST)) {
            return new DeclParser(getTokenIterator()).parseConstDecl(first, getNext());
        } else if (first.getType().equals(TokenCategory.INT)) {
            return new DeclParser(getTokenIterator()).parseVarDecl(first, (Ident) getNext());
        } else {
            previous();
            return parseStmt();
        }
    }
    
    public Stmt parseStmt() {
        /*
         * Stmt →
         * Block |
         * 'if' '(' Cond ')' Stmt [ 'else' Stmt ] |
         * 'while' '(' Cond ')' Stmt |
         * 'break' ';' |
         * 'continue' ';' |
         * 'return' [Exp] ';' |
         * 'printf''('FormatString{','Exp}')'';'
         * [Exp] ';' |
         * LVal '=' Exp ';' |
         * LVal '=' 'getint''('')'';' |
         */
        Token first = getNext();
        switch (first.getType()) {
            case L_BRACE:
                return new Stmt(parseBlock(first));
            case IF:
            case WHILE:
                return new Stmt(parseJudge(first));
            case BREAK:
                return new Stmt(parseBreak(first));
            case CONTINUE:
                return new Stmt(parseContinue(first));
            case RETURN:
                return new Stmt(parseReturn(first));
            case PRINT:
                return new Stmt(parseOutput(first));
            case SEMICN:
                return new Stmt(first);
            default:
                previous();
                Exp exp = new ExpParser(getTokenIterator()).parseExp();
                LVal lVal = exp.getLVal();
                if (lVal != null) {
                    Token assign = getNext();
                    if (!assign.getType().equals(TokenCategory.ASSIGN)) {
                        //TODO 是否考虑行数变化
                        previous();
                        Token semicolon = getSemicolon(first.getLine());//TODO 行数可能不准?
                        return new Stmt(new ExpStmt(exp, semicolon));
                    }
                    first = getNext();
                    
                    if (first.getType().equals(TokenCategory.GETINT)) {
                        Token getInt = first;
                        Token leftarent = getNext();
                        Token rightParent = getNext();//TODO 缺少右括号时报错
                        Token semicolon = getSemicolon(leftarent.getLine());
                        return new Stmt(new InputStmt(lVal, assign, getInt, leftarent, rightParent, semicolon));
                    } else {
                        previous();
                        exp = new ExpParser(getTokenIterator()).parseExp();
                        Token semicolon = getSemicolon(assign.getLine());//TODO 行数可能不对
                        return new Stmt(new AssignStmt(lVal, assign, exp, semicolon));
                    }
                } else {
                    Token semicolon = getSemicolon(first.getLine());//TODO 行数可能不准?
                    return new Stmt(new ExpStmt(exp, semicolon));
                }
        }
    }
    
    public JudgeStmt parseJudge(Token ifOrWhile) {
        Token leftParent = getNext();
        Cond cond = new ExpParser(getTokenIterator()).parseCond();
        Token rightParent = getNext();//TODO 缺少右括号时报错
        Stmt mainStmt = parseStmt();
        if (ifOrWhile.getType().equals(TokenCategory.WHILE)) {
            return new WhileStmt(ifOrWhile, leftParent, cond, rightParent, mainStmt);
        } else {
            Token first = getNext();
            if (first.getType().equals(TokenCategory.ELSE)) {
                Stmt elseStmt = parseStmt();
                return new IfStmt(ifOrWhile, leftParent, cond, rightParent, mainStmt, first, elseStmt);
            } else {
                previous();
                return new IfStmt(ifOrWhile, leftParent, cond, rightParent, mainStmt);
            }
        }
    }
    
    public BreakStmt parseBreak(Token breakSym) {
        Token semicolon = getSemicolon(breakSym.getLine());
        return new BreakStmt(breakSym, semicolon);
    }
    
    public ContinueStmt parseContinue(Token continueSym) {
        Token semicolon = getSemicolon(continueSym.getLine());
        return new ContinueStmt(continueSym, semicolon);
    }
    
    public ReturnStmt parseReturn(Token returnSym) {
        Token first = getNext();
        //if (first.getLine() == returnSym.getLine()) {
        //TODO 是否判断行数?
        if (first.getType().equals(TokenCategory.SEMICN)) {
            return new ReturnStmt(returnSym, first);
        } else {
            previous();
            Exp exp = new ExpParser(getTokenIterator()).parseExp();
            Token semicolon = getSemicolon(returnSym.getLine());
            return new ReturnStmt(returnSym, exp, semicolon);
        }
        //}
        //return null;//TODO 缺少分号报错
    }
    
    public OutputStmt parseOutput(Token print) {
        Token leftParent = getNext();
        FormatString str = (FormatString) getNext();
        LinkedList<Token> commas = new LinkedList<>();
        LinkedList<Exp> paras = new LinkedList<>();
        while (hasNext()) {
            Token comma = getNext();
            if (comma.getType().equals(TokenCategory.COMMA)) {
                commas.addLast(comma);
                paras.addLast(new ExpParser(getTokenIterator()).parseExp());
            } else {
                previous();
                break;
            }
        }
        Token rightParent = getNext();//TODO 缺少右括号时报错
        Token semicolon = getSemicolon(str.getLine());//TODO 行数不一定对
        return new OutputStmt(print, leftParent, str, commas, paras, rightParent, semicolon);
    }
    
    private Token getSemicolon(int lastLine) {
        Token first = getNext();
        if (!first.getType().equals(TokenCategory.SEMICN)) {
            //TODO 是否要判断隔行的?
            System.err.println("行：" + lastLine + "应为';'，实为无符号或：" + first);
            return null;//TODO 报错
        }
        return first;
    }
}
