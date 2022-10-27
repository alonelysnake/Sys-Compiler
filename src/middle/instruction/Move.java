package middle.instruction;

import middle.val.Value;

public class Move extends INode implements StackSpace {
    private final Value lVal;
    private final Value rVal;
    
    public Move(Value lVal, Value rVal) {
        this.lVal = lVal;
        this.rVal = rVal;
    }
    
    public Value getlVal() {
        return lVal;
    }
    
    public Value getrVal() {
        return rVal;
    }
    
    @Override
    public int getSize() {
        if (lVal.isTemp()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public Value getNewVar() {
        return lVal;
    }
    
    @Override
    public String toString() {
        return lVal + " = " + rVal + "\n";
    }
}
