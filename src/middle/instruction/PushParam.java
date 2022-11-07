package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;

public class PushParam extends INode implements UseNode {
    private Value para;//要传递的实参名
    
    public PushParam(Value para) {
        this.para = para;
    }
    
    public Value getPara() {
        return para;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(para);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        if (!(para instanceof Number)) {
            para = ops.get(0);
        }
    }
    
    @Override
    public String toString() {
        return "push " + para + "\n";
    }
}
