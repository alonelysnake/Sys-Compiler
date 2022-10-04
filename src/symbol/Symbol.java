package symbol;

import syntax.decl.BType;

public class Symbol {
    /**
     * 符号表表项
     */
    
    private final String name;//变量名
    private final boolean constFlag;//const int 还是 int
    private final BType type;//int array mat
    
    public Symbol(String name, boolean constFlag, int dim) {
        this.name = name;
        this.constFlag = constFlag;
        if (dim == 0) {
            type = BType.INT;
        } else if (dim == 1) {
            type = BType.ARR;
        } else if (dim == 2) {
            type = BType.MAT;
        } else {
            System.err.println("symbol构造：高于2维的数组");
            type = null;
        }
    }
    
    public BType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isConst() {
        return constFlag;
    }
}
