package middle.val;

public class Number extends Value {
    private int val;
    
    public Number(int val) {
        super(String.valueOf(val));
        this.val = val;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
