package middle.instruction;

import middle.val.Address;
import middle.val.Value;
import middle.val.Variable;

public class FetchParam extends INode implements StackSpace {
    private final Value para;//形参名
    
    public FetchParam(Value para) {
        this.para = para;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public Value getNewVar() {
        return para;
    }
    
    @Override
    public String toString() {
        if (para instanceof Address) {
            return "param addr " + para + "\n";
        } else if (para instanceof Variable) {
            return "param var" + para + "\n";
        } else {
            System.err.println("中间代码形参类型错误");
            return "";
        }
    }
}
