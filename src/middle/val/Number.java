package middle.val;

public class Number extends Value {
    private final int val;
    
    public Number(int val) {
        super(String.valueOf(val));
        this.val = val;
    }
    
    public int getVal() {
        return val;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
