package backend.element;

public class Imm implements MIPSUnit {
    private final int val;
    
    public Imm(int val) {
        this.val = val;
    }
    
    public int getVal() {
        return val;
    }
    
    @Override
    public String toString() {
        return String.valueOf(val);
    }
}
