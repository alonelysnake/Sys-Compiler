package middle.val;

public class Variable extends Value {
    //用于代表寄存器（变量）
    
    public Variable(String name) {
        super(name);
    }
    
    @Override
    public String toString() {
        return "$" + super.toString();
    }
}
