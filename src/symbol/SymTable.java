package symbol;

import java.util.HashMap;

public class SymTable {
    /**
     * 符号表
     */
    private static int cnt = 0;// 符号表数计数器
    private final HashMap<String, Symbol> symbols;//该符号表中的所有符号（符号名称（即变量名或是函数名）-符号对象）
    private final SymTable parent;//该符号表的父级表
    private final int depth;//当前符号表创建时已经出现的符号表的数量（用于语义分析给出变量的唯一深度）
    
    public SymTable() {
        //全局符号表的初始化，包含全局变量名（是否要包含函数名?）
        this.symbols = new HashMap<>();
        this.parent = null;
        this.depth = cnt;
        cnt++;
    }
    
    public SymTable(SymTable parent) {
        //局部（block块）的符号表
        this.symbols = new HashMap<>();
        this.parent = parent;
        this.depth = cnt;
        cnt++;
    }
    
    public void add(Symbol symbol) {
        //TODO 是否需要保护防止被覆盖?
        if (!symbols.containsKey(symbol.getName())) {
            symbol.setDepth(this.depth);
            this.symbols.put(symbol.getName(), symbol);
        }
    }
    
    public SymTable getParent() {
        return parent;
    }
    
    public boolean contains(String name, boolean recursive) {
        if (this.symbols.containsKey(name)) {
            return true;
        } else {
            return recursive && (this.parent != null) && parent.contains(name, true);
        }
    }
    
    public Symbol get(String name) {
        if (this.symbols.containsKey(name)) {
            return this.symbols.get(name);
        } else if (this.parent != null) {
            return parent.get(name);
        }
        return null;
    }
}
