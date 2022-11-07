package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;

public class Return extends INode implements UseNode {
    // jr，此外可能会有move v0
    public static final String RET_REG = "$v0";// 在函数调用后固定的寄存器v0
    
    private Value ret;//可能有的返回值
    
    // 无返回值
    public Return() {
        this.ret = null;
    }
    
    //有返回值
    public Return(Value ret) {
        this.ret = ret;
    }
    
    public Value getRet() {
        return ret;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        if (this.ret != null && !(this.ret instanceof Number)) {
            ret.add(this.ret);
        }
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        if (this.ret != null && !(this.ret instanceof Number)) {
            this.ret = ops.get(0);
        }
    }
    
    @Override
    public String toString() {
        if (ret != null) {
            return "return " + ret + "\n";
        }
        return "return\n";
    }
}
