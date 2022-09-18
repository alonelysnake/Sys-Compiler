package syntax.exp.multi;

import lexer.token.Token;

import java.util.Iterator;
import java.util.LinkedList;

public class ExpList<T> {
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
