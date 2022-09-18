package syntax.exp;

import lexer.token.Ident;
import lexer.token.IntConst;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.Parser;
import syntax.exp.multi.AddExp;
import syntax.exp.multi.AndExp;
import syntax.exp.multi.Cond;
import syntax.exp.multi.ConstExp;
import syntax.exp.multi.EqExp;
import syntax.exp.multi.Exp;
import syntax.exp.multi.LOrExp;
import syntax.exp.multi.MulExp;
import syntax.exp.multi.RelExp;
import syntax.exp.unary.Dimension;
import syntax.exp.unary.FuncCall;
import syntax.exp.unary.FuncRParas;
import syntax.exp.unary.LVal;
import syntax.exp.unary.Number;
import syntax.exp.unary.PrimaryExp;
import syntax.exp.unary.PrimaryUnit;
import syntax.exp.unary.SubExp;
import syntax.exp.unary.UnaryExp;

import java.util.LinkedList;
import java.util.ListIterator;

public class ExpParser extends Parser {
    public ExpParser(ListIterator<Token> tokenIterator) {
        super(tokenIterator);
    }
    
    public Exp parseExp() {
        return new Exp(parseAddExp());
    }
    
    public ConstExp parseConstExp() {
        return new ConstExp(parseAddExp());
    }
    
    public Cond parseCond() {
        return new Cond(parseLOrExp());
    }
    
    public LOrExp parseLOrExp() {
        //LOrExp → LAndExp {'||' LAndExp}
        LinkedList<AndExp> andExps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        andExps.addLast(parseAndExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.OR)) {
                ops.addLast(op);
                andExps.addLast(parseAndExp());
            } else {
                previous();
                break;
            }
        }
        return new LOrExp(ops, andExps);
    }
    
    public AndExp parseAndExp() {
        //LAndExp → EqExp {'&&' EqExp}
        LinkedList<EqExp> eqExps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        eqExps.addLast(parseEqExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.AND)) {
                ops.addLast(op);
                eqExps.addLast(parseEqExp());
            } else {
                previous();
                break;
            }
        }
        return new AndExp(ops, eqExps);
    }
    
    public EqExp parseEqExp() {
        //EqExp → RelExp {('==' | '!=') RelExp}
        LinkedList<RelExp> relExps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        relExps.addLast(parseRelExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.EQ) ||
                    op.getType().equals(TokenCategory.NEQ)) {
                ops.addLast(op);
                relExps.addLast(parseRelExp());
            } else {
                previous();
                break;
            }
        }
        return new EqExp(ops, relExps);
    }
    
    public RelExp parseRelExp() {
        //RelExp → AddExp {('<' | '>' | '<=' | '>=') AddExp}
        LinkedList<AddExp> addExps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        addExps.addLast(parseAddExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.LT) || op.getType().equals(TokenCategory.LE) ||
                    op.getType().equals(TokenCategory.GT) || op.getType().equals(TokenCategory.GE)) {
                ops.addLast(op);
                addExps.addLast(parseAddExp());
            } else {
                previous();
                break;
            }
        }
        return new RelExp(ops, addExps);
    }
    
    public AddExp parseAddExp() {
        //AddExp → MulExp | AddExp ('+' | '−') MulExp
        //AddExp → MulExp {('+' | '−') MulExp}
        LinkedList<MulExp> mulExps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        mulExps.addLast(parseMulExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.PLUS) ||
                    op.getType().equals(TokenCategory.MINUS)) {
                ops.addLast(op);
                mulExps.addLast(parseMulExp());
            } else {
                previous();
                break;
            }
        }
        return new AddExp(ops, mulExps);
    }
    
    public MulExp parseMulExp() {
        //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        //MulExp → UnaryExp {('*' | '/' | '%') UnaryExp}
        LinkedList<UnaryExp> exps = new LinkedList<>();
        LinkedList<Token> ops = new LinkedList<>();
        exps.addLast(parseUnaryExp());
        while (hasNext()) {
            Token op = getNext();
            if (op.getType().equals(TokenCategory.MULT) ||
                    op.getType().equals(TokenCategory.DIV) ||
                    op.getType().equals(TokenCategory.MOD)) {
                ops.addLast(op);
                exps.addLast(parseUnaryExp());
            } else {
                previous();
                break;
            }
        }
        return new MulExp(ops, exps);
    }
    
    public UnaryExp parseUnaryExp() {
        //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
        //UnaryExp → {UnaryOp} (PrimaryExp | Ident '(' [FuncRParams] ')')
        LinkedList<Token> ops = new LinkedList<>();
        Token first;
        while (hasNext()) {
            first = getNext();
            //TODO 是否需要条件表达式的判断?
            if (first.getType().equals(TokenCategory.PLUS) ||
                    first.getType().equals(TokenCategory.MINUS) ||
                    first.getType().equals(TokenCategory.NOT)) {
                ops.addLast(first);
            } else {
                previous();
                break;
            }
        }
        first = getNext();
        if (first.getType().equals(TokenCategory.IDENT)) {
            //<FuncCall> | <LVal>
            //Ident ('(' | 其他)
            Token second = getNext();
            if (second.getType().equals(TokenCategory.L_PARENT)) {
                FuncCall func = parseFuncCall((Ident) first, second);
                return new UnaryExp(func, ops);
            }
            previous();
        }
        PrimaryExp exp = parsePrimaryExp(first);
        return new UnaryExp(exp, ops);
    }
    
    public FuncCall parseFuncCall(Ident name, Token leftParent) {
        //Ident '(' [FuncRParams] ')'
        Token first = getNext();
        if (first.getType().equals(TokenCategory.R_PARENT)) {
            //无参调用
            return new FuncCall(name, leftParent, first);
        }
        //TODO 没有右括号的报错
        
        //有参调用
        previous();
        FuncRParas paras = parseFuncParas();
        first = getNext();
        if (first.getType().equals(TokenCategory.R_PARENT)) {
            return new FuncCall(name, leftParent, paras, first);
        } else {
            //TODO 没有右括号的报错
            return null;
        }
    }
    
    public FuncRParas parseFuncParas() {
        LinkedList<Exp> paras = new LinkedList<>();
        LinkedList<Token> commas = new LinkedList<>();
        paras.addLast(parseExp());
        while (hasNext()) {
            Token comma = getNext();
            if (!comma.getType().equals(TokenCategory.COMMA)) {
                previous();
                break;
            }
            commas.addLast(comma);
            paras.addLast(parseExp());
        }
        return new FuncRParas(paras, commas);
    }
    
    public PrimaryExp parsePrimaryExp(Token first) {
        //PrimaryExp → '(' Exp ')' | LVal | Number
        PrimaryUnit unit = null;
        if (first.getType().equals(TokenCategory.L_PARENT)) {
            unit = parseSubExp(first);
        } else if (first.getType().equals(TokenCategory.IDENT)) {
            unit = parseLVal((Ident) first);
        } else if (first.getType().equals(TokenCategory.INTCONST)) {
            unit = new Number((IntConst) first);
        } else {
            System.err.println("行：" + first.getLine() + "未读到PrimaryExp，实为：" + first);
        }
        return new PrimaryExp(unit);
    }
    
    public SubExp parseSubExp(Token leftParent) {
        //括号中间不能为空
        //TODO 可能不需要做此判断?
        Token first = getNext();
        if (first.getType().equals(TokenCategory.R_PARENT)) {
            System.err.println("subExp的括号中间为空");
            return null;
        }
        previous();
        
        Exp exp = parseExp();
        
        first = getNext();
        if (!first.getType().equals(TokenCategory.R_PARENT)) {
            System.err.println("subExp未读到右括号，实为：" + first);
            previous();
            first = null;
        }
        return new SubExp(leftParent, exp, first);
    }
    
    public LVal parseLVal(Ident name) {
        //LVal → Ident {'[' Exp ']'}
        Token first = getNext();
        if (!first.getType().equals(TokenCategory.L_BRACK)) {
            previous();
            return new LVal(name);
        }
        LinkedList<Dimension> dimensions = new LinkedList<>();
        while (hasNext()) {
            if (!first.getType().equals(TokenCategory.L_BRACK)) {
                previous();
                break;
            }
            dimensions.addLast(parseVarDimension(first));
            first = getNext();
        }
        return new LVal(name, dimensions);
    }
    
    public Dimension parseVarDimension(Token leftBrack) {
        Exp exp = parseExp();
        Token rightBrack = getNext();//TODO 无右括号报错
        return new Dimension(leftBrack, exp, rightBrack);
    }
}
