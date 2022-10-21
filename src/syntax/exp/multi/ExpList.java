package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.BinaryOp;
import middle.instruction.INode;
import middle.val.Value;
import middle.val.Variable;
import syntax.SyntaxNode;
import syntax.exp.unary.UnaryExp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public abstract class ExpList<T> implements SyntaxNode {
    /**
     * 存放表达式的容器
     */
    private final LinkedList<Token> ops;
    private final LinkedList<T> units;
    private final String className;
    
    private static final Map<TokenCategory, BinaryOp.Operator> token2BinaryOp =
            Collections.unmodifiableMap(new HashMap<TokenCategory, BinaryOp.Operator>() {
                {
                    put(TokenCategory.PLUS, BinaryOp.Operator.ADD);
                    put(TokenCategory.MINUS, BinaryOp.Operator.SUB);
                    put(TokenCategory.MULT, BinaryOp.Operator.MULT);
                    put(TokenCategory.DIV, BinaryOp.Operator.DIV);
                    put(TokenCategory.MOD, BinaryOp.Operator.MOD);
                    put(TokenCategory.AND, BinaryOp.Operator.AND);
                    put(TokenCategory.OR, BinaryOp.Operator.OR);
                    put(TokenCategory.EQ, BinaryOp.Operator.EQ);
                    put(TokenCategory.NEQ, BinaryOp.Operator.NEQ);
                    put(TokenCategory.GT, BinaryOp.Operator.GT);
                    put(TokenCategory.GE, BinaryOp.Operator.GE);
                    put(TokenCategory.LT, BinaryOp.Operator.LT);
                    put(TokenCategory.LE, BinaryOp.Operator.LE);
                }
            });
    
    public ExpList(LinkedList<Token> ops, LinkedList<T> units, String name) {
        this.ops = ops;
        this.units = units;
        this.className = name;
    }
    
    public void addLast(Token op) {
        ops.addLast(op);
    }
    
    public void addLast(T unit) {
        units.addLast(unit);
    }
    
    public int size() {
        return units.size();
    }
    
    public T getFirst() {
        return units.getFirst();
    }
    
    public LinkedList<Token> getOps() {
        return ops;
    }
    
    public LinkedList<T> getUnits() {
        return units;
    }
    
    public abstract LinkedList<Ident> getNames();//TODO 仿照getMaxLine精简
    
    public int getMaxLine() {
        T last = units.getLast();
        if (last instanceof ExpList) {
            return ((ExpList<?>) last).getMaxLine();
        }
        if (last instanceof UnaryExp) {
            return ((UnaryExp) last).getMaxLine();
        }
        return 0;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        for (T t : units) {
            if (t instanceof SyntaxNode) {
                ((SyntaxNode) t).analyse(state);
            } else {
                return;
            }
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        Iterator<T> unitIterator = getUnits().iterator();
        Iterator<Token> opIterator = getOps().iterator();
        
        BlockInfo unitBlock = ((SyntaxNode) unitIterator.next()).generateIcode(state);
        INode first = unitBlock.getFirst();
        INode last = unitBlock.getLast();
        Value val = unitBlock.getRetVal();
        
        while (unitIterator.hasNext()) {
            Token op = opIterator.next();
            T unit = unitIterator.next();
            BinaryOp.Operator binaryOp = token2BinaryOp.get(op);//保证存在
            unitBlock = ((SyntaxNode) unit).generateIcode(state);
            last = last.insert(unitBlock.getFirst());
            Variable newVar = new Variable(String.valueOf(MiddleState.tmpCnt++));
            BinaryOp binaryCode = new BinaryOp(newVar, binaryOp, val, unitBlock.getRetVal());
            last = last.insert(binaryCode);
            val = newVar;
        }
        return new BlockInfo(val, first, last);
    }
    
    @Override
    public String toString() {
        Iterator<T> unitIterator = units.iterator();
        StringBuilder sb = new StringBuilder(unitIterator.next().toString());
        sb.append(className);
        Iterator<Token> opIterator = ops.iterator();
        while (unitIterator.hasNext()) {
            sb.append(opIterator.next());
            sb.append(unitIterator.next());
            sb.append(className);
        }
        return sb.toString();
    }
}
