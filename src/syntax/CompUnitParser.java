package syntax;

import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.decl.Decl;
import syntax.decl.DeclParser;
import syntax.func.FuncDef;
import syntax.func.FuncParser;
import syntax.func.MainFunc;

import java.util.LinkedList;

public class CompUnitParser extends Parser {
    
    public CompUnitParser(LinkedList<Token> tokens) {
        super(tokens);
    }
    
    public CompUnit parseCompUnit() {
        //CompUnit → {Decl} {FuncDef} MainFuncDef
        Token first = getNext();
        Token second = getNext();
        Token third;
        LinkedList<Decl> globals = new LinkedList<>();
        while (hasNext()) {
            //'const'
            //'int' Ident ~'('
            if (first.getType().equals(TokenCategory.CONST)) {
                if (second.getType().equals(TokenCategory.INT)) {
                    //const int
                    globals.addLast(new DeclParser(getTokenIterator()).parseConstDecl(first, second));
                } else {
                    //TODO 报错
                    System.err.println("<Decl> const 后不是 int");
                }
            } else if (first.getType().equals(TokenCategory.INT)) {
                if (second.getType().equals(TokenCategory.IDENT)) {
                    third = getNext();
                    if (!third.getType().equals(TokenCategory.L_PARENT)) {
                        //'int' <Ident> <decl的第三个词>
                        //回退第三个词，交给下一级parser处理
                        previous();
                        globals.addLast(new DeclParser(getTokenIterator()).parseVarDecl(first, (Ident) second));
                    } else {
                        //<FuncDef>回退'('，
                        previous();
                        break;
                    }
                } else {
                    //既不是<Decl>也不是<FuncDef>
                    break;
                }
            } else {
                break;
            }
            first = getNext();
            second = getNext();
        }
        
        //此时共读入first和second两个词
        LinkedList<FuncDef> funcs = new LinkedList<>();
        MainFunc mainFunc = null;
        FuncParser funcParser = new FuncParser(getTokenIterator());
        while (hasNext()) {
            if (first.getType().equals(TokenCategory.INT) && second.getType().equals(TokenCategory.MAIN)) {
                //是main函数
                mainFunc = funcParser.parseMainFunc(first, second);
                break;
            } else {
                if (first.getType().equals(TokenCategory.INT) || first.getType().equals(TokenCategory.VOID)) {
                    if (second.getType().equals(TokenCategory.IDENT)) {
                        //TODO 是否需要检测<FuncDef>和<Decl>交替的情况?
                        funcs.addLast(funcParser.parseFunc(first, (Ident) second));
                    } else {
                        //TODO 报错
                        System.err.println("函数声明格式错误，未读到ident");
                    }
                } else {
                    //TODO 报错
                    System.err.println("函数声明格式错误，应为int/void，实为：" + first);
                }
            }
            first = getNext();
            second = getNext();
        }
        return new CompUnit(globals, funcs, mainFunc);
    }
}
