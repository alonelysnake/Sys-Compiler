package middle.instruction;

import middle.val.Value;

public class Move extends INode {
    private final Value lVal;
    private final Value rVal;
    
    public Move(Value lVal, Value rVal) {
        this.lVal = lVal;
        this.rVal = rVal;
    }
    
    @Override
    public String toString() {
        return lVal + " = " + rVal;
    }
}
