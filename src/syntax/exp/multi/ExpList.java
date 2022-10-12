package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import syntax.SyntaxNode;
import syntax.exp.unary.UnaryExp;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class ExpList<T> implements SyntaxNode {
    /**
     * 存放表达式的容器
     */
    private final LinkedList<Token> ops;
    private final LinkedList<T> units;
    private final String className;
    
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
