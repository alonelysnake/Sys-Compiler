package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;

public class Move extends INode implements DefNode, UseNode {
    private final Value lVal;
    private Value rVal;
    
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
    public Value getDef() {
        return lVal;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(rVal);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        if (!(rVal instanceof Number)) {
            rVal = ops.get(0);
        }
    }
    
    @Override
    public String toString() {
        return lVal + " = " + rVal + "\n";
    }
}
