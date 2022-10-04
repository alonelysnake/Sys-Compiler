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
import syntax.exp.unary.ExpUnit;
import syntax.exp.unary.LVal;
import syntax.exp.unary.PrimaryExp;
import syntax.exp.unary.PrimaryUnit;
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
                //不会出现缺少右大括号的情况
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
                ExpUnit firstUnit = exp.getFirstExpUnit();
                LVal lVal = null;
                if ((firstUnit instanceof PrimaryExp)) {
                    PrimaryUnit firstPriUnit = ((PrimaryExp) firstUnit).getUnit();
                    if (firstPriUnit instanceof LVal) {
                        lVal = (LVal) firstPriUnit;
                    }
                }
                if (lVal != null) {
                    Token assign = getNext();
                    if (!assign.getType().equals(TokenCategory.ASSIGN)) {
                        //TODO 是否考虑行数变化
                        previous();
                        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
                        return new Stmt(new ExpStmt(exp, semicolon));
                    }
                    first = getNext();
                    
                    if (first.getType().equals(TokenCategory.GETINT)) {
                        Token getInt = first;
                        Token leftarent = getNext();
                        Token rightParent = getSpecialToken(TokenCategory.R_PARENT);
                        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
                        return new Stmt(new InputStmt(lVal, assign, getInt, leftarent, rightParent, semicolon));
                    } else {
                        previous();
                        exp = new ExpParser(getTokenIterator()).parseExp();
                        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
                        return new Stmt(new AssignStmt(lVal, assign, exp, semicolon));
                    }
                } else {
                    Token semicolon = getSpecialToken(TokenCategory.SEMICN);
                    return new Stmt(new ExpStmt(exp, semicolon));
                }
        }
    }
    
    public JudgeStmt parseJudge(Token ifOrWhile) {
        Token leftParent = getNext();
        Cond cond = new ExpParser(getTokenIterator()).parseCond();
        Token rightParent = getSpecialToken(TokenCategory.R_PARENT);
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
        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
        return new BreakStmt(breakSym, semicolon);
    }
    
    public ContinueStmt parseContinue(Token continueSym) {
        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
        return new ContinueStmt(continueSym, semicolon);
    }
    
    public ReturnStmt parseReturn(Token returnSym) {
        Token first = getNext();
        //TODO 是否判断行数?
        if (first.getType().equals(TokenCategory.SEMICN)) {
            return new ReturnStmt(returnSym, first);
        } else {
            previous();
            if (first.getType().equals(TokenCategory.IDENT) ||
                    first.getType().equals(TokenCategory.INTCONST) ||
                    first.getType().equals(TokenCategory.L_PARENT) ||
                    first.getType().equals(TokenCategory.PLUS) ||
                    first.getType().equals(TokenCategory.MINUS) ||
                    first.getType().equals(TokenCategory.NOT)) {
                int curIndex = getTokenIterator().nextIndex();
                Exp exp = new ExpParser(getTokenIterator()).parseExp();
                first = getSpecialToken(TokenCategory.ASSIGN);
                //可能会出现return 赋值语句=xxx;的情况，此时return为缺少';'
                if (first != null) {
                    back2Point(curIndex);
                    return new ReturnStmt(returnSym, null);
                }
                Token semicolon = getSpecialToken(TokenCategory.SEMICN);
                return new ReturnStmt(returnSym, exp, semicolon);
            } else {
                return new ReturnStmt(returnSym, null);
            }
        }
    }
    
    public OutputStmt parseOutput(Token print) {
        Token leftParent = getNext();
        FormatString str = (FormatString) getNext();
        LinkedList<Token> commas = new LinkedList<>();
        LinkedList<Exp> paras = new LinkedList<>();
        while (hasNext()) {
            Token comma = getSpecialToken(TokenCategory.COMMA);
            if (comma != null) {
                commas.addLast(comma);
                paras.addLast(new ExpParser(getTokenIterator()).parseExp());
            } else {
                break;
            }
        }
        Token rightParent = getSpecialToken(TokenCategory.R_PARENT);
        Token semicolon = getSpecialToken(TokenCategory.SEMICN);
        return new OutputStmt(print, leftParent, str, commas, paras, rightParent, semicolon);
    }
}
