package symbol;

public class Symbol {
    /**
     * 符号表表项
     */
    
    private final String name;//变量名
    private final boolean constFlag;//const int 还是 int
    
    public Symbol(String name, boolean constFlag) {
        this.name = name;
        this.constFlag = constFlag;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isConst() {
        return constFlag;
    }
}
