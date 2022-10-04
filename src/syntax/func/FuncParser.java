package syntax.func;

import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.Parser;
import syntax.decl.DeclParser;
import syntax.exp.unary.Dimension;
import syntax.stmt.StmtParser;
import syntax.stmt.multi.Block;

import java.util.LinkedList;
import java.util.ListIterator;

public class FuncParser extends Parser {
    public FuncParser(ListIterator<Token> tokenIterator) {
        super(tokenIterator);
    }
    
    public MainFunc parseMainFunc(Token intSym, Token main) {
        Token first = getNext();
        Token leftParent = first;
        Token rightParent = getSpecialToken(TokenCategory.R_PARENT);
        first = getNext();
        Block block = null;
        if (first.getType().equals(TokenCategory.L_BRACE)) {
            block = new StmtParser(getTokenIterator()).parseBlock(first);
        } else {
            System.err.println("main函数的block左括号未读到，读到词" + first);
        }
        return new MainFunc(intSym, main, leftParent, rightParent, block);
    }
    
    public FuncDef parseFunc(Token funcType, Ident funcName) {
        Token leftParent = getNext();
        Token first = getNext();
        FuncFParams paras = null;
        Token rightParent = null;
        if (first.getType().equals(TokenCategory.INT)) {
            previous();
            paras = parseParas();
            rightParent = getSpecialToken(TokenCategory.R_PARENT);
        } else if (first.getType().equals(TokenCategory.R_PARENT)) {
            rightParent = first;
        } else {
            //既不是'int'也不是'）'
            //System.err.println("自定义函数未读到')'，实为：" + first + "行：" + first.getLine());
            previous();
        }
        
        first = getNext();
        Block block = new StmtParser(getTokenIterator()).parseBlock(first);
        
        return new FuncDef(funcType, funcName, leftParent, paras, rightParent, block);
    }
    
    public FuncFParams parseParas() {
        //FuncFParams → FuncFParam { ',' FuncFParam }
        LinkedList<FuncFParam> params = new LinkedList<>();
        LinkedList<Token> commas = new LinkedList<>();
        params.addLast(parsePara());
        Token first;
        while (hasNext()) {
            first = getSpecialToken(TokenCategory.COMMA);
            if (first != null) {
                commas.addLast(first);
                params.addLast(parsePara());
            } else {
                break;
            }
        }
        return new FuncFParams(commas, params);
    }
    
    public FuncFParam parsePara() {
        //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
        Token first = getNext();
        Token bType = null;
        if (first.getType().equals(TokenCategory.INT)) {
            bType = first;
        } else {
            System.err.println("形参数据类型错误，应为int，实为：" + first);
        }
        
        Ident name;
        first = getNext();
        if (first.getType().equals(TokenCategory.IDENT)) {
            name = (Ident) first;
        } else {
            System.err.println("形参数据类型错误，应为Ident，实为：" + first);
            return null;
        }
        
        Token left = getSpecialToken(TokenCategory.L_BRACK);//此时first是'['
        if (left == null) {
            return new FuncFParam(bType, name);
        }
        Token right = getSpecialToken(TokenCategory.R_BRACK);
        Dimension firstDimension = new Dimension(left, right);
        
        first = getSpecialToken(TokenCategory.L_BRACK);
        if (first == null) {
            return new FuncFParam(bType, name, firstDimension);
        }
        LinkedList<Dimension> dimensions = new LinkedList<>();
        while (hasNext()) {
            dimensions.addLast(new DeclParser(getTokenIterator()).parseConstDimention(first));
            first = getSpecialToken(TokenCategory.L_BRACK);
            if (first == null) {
                break;
            }
        }
        return new FuncFParam(bType, name, firstDimension, dimensions);
    }
}
