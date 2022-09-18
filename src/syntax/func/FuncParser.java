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
        first = getNext();
        Token rightParent = null;
        if (first.getType().equals(TokenCategory.R_PARENT)) {
            rightParent = first;
        }
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
        Token first;
        Token leftParent = getNext();
        first = getNext();
        FuncFParams paras = null;
        Token rightParent = null;
        if (first.getType().equals(TokenCategory.INT)) {
            previous();
            paras = parseParas();
            rightParent = getNext();//TODO 缺少右括号时报错
        } else if (first.getType().equals(TokenCategory.R_PARENT)) {
            rightParent = first;
        } else {
            //既不是'int'也不是'）'
            System.err.println("自定义函数未读到')'，实为：" + first + "行：" + first.getLine());
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
            first = getNext();
            if (first.getType().equals(TokenCategory.COMMA)) {
                commas.addLast(first);
                params.addLast(parsePara());
            } else if (first.getType().equals(TokenCategory.R_PARENT)) {
                previous();
                break;
            } else {
                System.err.println("行：" + first.getLine() + "函数定义形参后应为','或')'，实为：" + first);
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
        
        first = getNext();
        if (!first.getType().equals(TokenCategory.L_BRACK)) {
            //TODO 是否考虑错误情况?
            previous();
            return new FuncFParam(bType, name);
        }
        Token left = first;//此时first是'['
        first = getNext();
        if (!first.getType().equals(TokenCategory.R_BRACK)) {
            //TODO 报错
            System.err.println("形参数组第一维含参");
            return null;
        }
        Token right = first;
        Dimension firstDimension = new Dimension(left, right);
        
        first = getNext();
        if (!first.getType().equals(TokenCategory.L_BRACK)) {
            previous();
            return new FuncFParam(bType, name, firstDimension);
        }
        LinkedList<Dimension> dimensions = new LinkedList<>();
        while (hasNext()) {
            if (!first.getType().equals(TokenCategory.L_BRACK)) {
                previous();
                break;
            }
            dimensions.addLast(new DeclParser(getTokenIterator()).parseConstDimention(first));
            first = getNext();
        }
        return new FuncFParam(bType, name, firstDimension, dimensions);
    }
}
