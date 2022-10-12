package middle.instruction;

import middle.val.Value;

public class PushParam extends INode {
    private final Value para;//要传递的实参名
    
    public PushParam(Value para) {
        this.para = para;
    }
    
    @Override
    public String toString() {
        return "push " + para + "\n";
    }
}
