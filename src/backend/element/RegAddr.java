package backend.element;

public class RegAddr implements Address {
    private final Reg base;
    private final int offset;
    
    public RegAddr(Reg base, int offset) {
        this.base = base;
        this.offset = offset;
    }
    
    public RegAddr(RegAddr base, int offset) {
        this.base = base.base;
        this.offset = base.offset + offset;
    }
    
    @Override
    public String toString() {
        return offset + "(" + base + ")";
    }
}
